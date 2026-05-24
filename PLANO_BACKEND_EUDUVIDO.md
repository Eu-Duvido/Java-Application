# Plano de Implementação — Backend EuDuvido

> **Uso deste documento:** abra este arquivo no IntelliJ (aba lateral) enquanto usa o plugin Claude Code. Para cada bloco de trabalho, há um **prompt pronto** na subseção "Prompt para o plugin" que você pode copiar e colar no chat do plugin. Os blocos estão em ordem de dependência — execute de cima para baixo. Ao final de cada bloco há um **critério de aceitação** para você validar antes de passar pro próximo.
>
> **Contexto fixo (cole isso na primeira interação com o plugin):**
> - Projeto: `euduvido-api` (Spring Boot 4.0.1, Java 21, Maven).
> - Arquitetura: Clean Architecture com 4 camadas — `domain` (puro, sem anotações Spring/JPA), `application/usecases`, `infrastructure/persistence` (JPA entities + mappers + impls de repositório), `entrypoint` (controllers e DTOs).
> - **Nenhum arquivo da camada `domain` pode depender de Spring, JPA, Jackson ou Lombok.** Use POJOs puros.
> - Entidades JPA ficam em `infrastructure/persistence/entities/*Entity.java` e têm um método `toDomain()` + estático `fromDomain()` para mapear.
> - Use cases ficam em `application/usecases/<aggregate>/` e são POJOs sem anotação; o wiring é feito em `config/UseCaseConfig.java` com `@Bean`.
> - Repositórios no domínio são **interfaces** (`domain/repositories/*Repository.java`). Implementações em `infrastructure/repositories/*RepositoryImpl.java` com `@Component` (ou `@Repository`).
> - DTOs de request/response ficam em `entrypoint/dtos/request/` e `entrypoint/dtos/response/`. **Nunca** colocar entidade de domínio como campo de DTO — sempre usar IDs e tipos primitivos/padrão.
> - Banco: **MySQL** com JPA (dev e prod).
> - Storage de mídia: **disco local** (em dev) — via interface `FileStorageService` na `application/services/`, implementação em `infrastructure/storage/`.
> - IA: **Claude** (Anthropic API) — via interface `AiValidationService`, implementação em `infrastructure/ai/`. A API key virá de env var `ANTHROPIC_API_KEY` (não hardcodar).
> - Segurança: JWT (HS256, 24h) + BCrypt. Token no header `Authorization: Bearer <token>`. Sem refresh token nessa primeira versão.
>
> **Regras do ouro que o plugin deve respeitar em todos os blocos:**
> 1. Nunca criar import de Spring/JPA/Jackson na camada `domain`.
> 2. Sempre adicionar `@Bean` no `UseCaseConfig` quando criar um novo UseCase.
> 3. Sempre criar DTO de response — nunca retornar entidade de domínio direto.
> 4. Toda exceção nova do domínio deve ter handler em `GlobalExceptionHandler`.
> 5. Todo endpoint novo precisa de documentação Javadoc breve e — depois do Bloco 11 — anotação `@Operation` do Springdoc.
> 6. Depois de cada bloco, rodar `./mvnw compile` (e `./mvnw test` quando houver testes) e corrigir qualquer erro antes de prosseguir.

---

## Sumário

- [Bloco 0 — Setup: MySQL, Flyway e limpeza de credenciais](#bloco-0--setup-mysql-flyway-e-limpeza-de-credenciais)
- [Bloco 1 — Correção dos bugs críticos](#bloco-1--correção-dos-bugs-críticos)
- [Bloco 2 — Refatoração dos DTOs (IDs em vez de entidades)](#bloco-2--refatoração-dos-dtos-ids-em-vez-de-entidades)
- [Bloco 3 — Segurança: BCrypt, JWT, SecurityFilterChain, AuthController](#bloco-3--segurança-bcrypt-jwt-securityfilterchain-authcontroller)
- [Bloco 4 — Limpeza: remover entidade Invite (consolidação em ChallengeParticipation)](#bloco-4--limpeza-remover-entidade-invite-consolidação-em-challengeparticipation)
- [Bloco 5 — Domínio acadêmico: difficulty, subject, goal, progress](#bloco-5--domínio-acadêmico-difficulty-subject-goal-progress)
- [Bloco 6 — Expiração automática de desafios (@Scheduled)](#bloco-6--expiração-automática-de-desafios-scheduled)
- [Bloco 7 — Filtros por status e paginação](#bloco-7--filtros-por-status-e-paginação)
- [Bloco 8 — Storage local e upload multipart de mídia](#bloco-8--storage-local-e-upload-multipart-de-mídia)
- [Bloco 9 — Validação por IA (Claude)](#bloco-9--validação-por-ia-claude)
- [Bloco 10 — Testes unitários e de integração](#bloco-10--testes-unitários-e-de-integração)
- [Bloco 11 — OpenAPI/Swagger](#bloco-11--openapiswagger)
- [Bloco 12 — Polimento: exceções, logs, CORS, README](#bloco-12--polimento-exceções-logs-cors-readme)
- [Apêndice A — Convenções de código](#apêndice-a--convenções-de-código)
- [Apêndice B — Comandos úteis](#apêndice-b--comandos-úteis)
- [Apêndice C — Checklist final (rastreabilidade RF × bloco)](#apêndice-c--checklist-final-rastreabilidade-rf--bloco)

---

## Bloco 0 — Setup: MySQL, Flyway e limpeza de credenciais

**Objetivo:** trocar H2 por MySQL, adicionar Flyway para migrations, remover credenciais commitadas e preparar ambiente local com Docker Compose.

**Arquivos afetados:**

- `pom.xml`
- `src/main/resources/application.properties` → substituir por `application.yml` (mais legível para múltiplos profiles)
- `src/main/resources/db/migration/V1__init.sql` (novo)
- `.env.example` (novo)
- `docker-compose.yml` (novo, na raiz de `euduvido-api/`)
- `.gitignore` — adicionar `.env`, `uploads/`

**Passos:**

1. Em `pom.xml`, remover dependência `com.h2database:h2` e adicionar:
   - `com.mysql:mysql-connector-j` (runtime)
   - `org.flywaydb:flyway-core`
   - `org.flywaydb:flyway-mysql`

2. Criar `docker-compose.yml`:
   ```yaml
   services:
     mysql:
       image: mysql:8.4
       container_name: euduvido-mysql
       environment:
         MYSQL_DATABASE: euduvido
         MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD:-rootpass}
         MYSQL_USER: ${DB_USER:-euduvido}
         MYSQL_PASSWORD: ${DB_PASSWORD:-euduvidopass}
       ports: ["3306:3306"]
       volumes: [mysql_data:/var/lib/mysql]
       healthcheck:
         test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
         interval: 5s
         timeout: 5s
         retries: 10
   volumes:
     mysql_data:
   ```

3. Criar `.env.example` (sem valores reais) e instruir que `.env` seja ignorado pelo git:
   ```
   DB_URL=jdbc:mysql://localhost:3306/euduvido?useSSL=false&serverTimezone=UTC
   DB_USER=euduvido
   DB_PASSWORD=euduvidopass
   JWT_SECRET=trocar-em-producao-64-chars-minimo-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
   ANTHROPIC_API_KEY=
   APP_STORAGE_LOCAL_PATH=./uploads
   ```

4. Substituir `application.properties` por `application.yml` com profiles:
   ```yaml
   spring:
     application:
       name: euduvido-api
     datasource:
       url: ${DB_URL}
       username: ${DB_USER}
       password: ${DB_PASSWORD}
     jpa:
       hibernate:
         ddl-auto: validate   # Flyway controla o schema
       properties:
         hibernate:
           dialect: org.hibernate.dialect.MySQLDialect
           format_sql: true
     flyway:
       enabled: true
       baseline-on-migrate: true
       locations: classpath:db/migration
   server:
     port: 8080
   app:
     storage:
       local:
         path: ${APP_STORAGE_LOCAL_PATH:./uploads}
     security:
       jwt:
         secret: ${JWT_SECRET}
         expiration-minutes: 1440  # 24h
     anthropic:
       api-key: ${ANTHROPIC_API_KEY:}
       base-url: https://api.anthropic.com/v1
       model: claude-sonnet-4-6
   logging:
     level:
       com.euduvido.euduvido_api: DEBUG
   ```

5. Criar `src/main/resources/db/migration/V1__init.sql` com o schema atual (users, challenges, challenge_participations, proofs, challenge_participants — NÃO criar tabela `invites`, vai ser removida no Bloco 4). Espelhar exatamente os campos das entidades JPA atuais. Usar `BIGINT PRIMARY KEY AUTO_INCREMENT`, `VARCHAR(255)`, `TIMESTAMP`, etc.

6. Atualizar `.gitignore` com `.env`, `uploads/`, `target/`.

7. **Remover a senha `Soph.2901`** que está commitada no `application.properties` atual — é credencial vazada.

**Critério de aceitação:**

- `docker compose up -d mysql` sobe o MySQL.
- `./mvnw spring-boot:run` inicia sem erros e conecta ao MySQL.
- Flyway aplica `V1__init.sql` no primeiro start (ver logs `Successfully applied 1 migration`).
- Arquivo `.env` existe localmente mas **não** entra no commit.
- Não existe mais nenhuma senha em claro no repositório.

**Prompt para o plugin:**

```
Faça o Bloco 0 do PLANO_BACKEND_EUDUVIDO.md: migrar de H2 para MySQL.
- Ajuste pom.xml: remova H2, adicione mysql-connector-j e flyway (core + flyway-mysql).
- Substitua application.properties por application.yml usando ${ENV_VARS}, com profile padrão para MySQL, flyway habilitado e hibernate.ddl-auto=validate.
- Gere V1__init.sql em src/main/resources/db/migration espelhando o schema atual das @Entity (users, challenges, challenge_participations, proofs, challenge_participants — ignore Invite, vai ser removida depois).
- Crie docker-compose.yml na raiz de euduvido-api com MySQL 8.4.
- Crie .env.example e atualize .gitignore.
- Remova a senha "Soph.2901" do projeto.
- Ao final, rode ./mvnw compile e me reporte qualquer erro.
```

---

## Bloco 1 — Correção dos bugs críticos

**Objetivo:** corrigir os 4 bugs confirmados que impedem funcionamento correto da API.

**Bugs a corrigir:**

### 1.1 `SubmitProofUseCase.execute` — comparação consigo mesmo

Arquivo: `application/usecases/proof/SubmitProofUseCase.java:38`

Trocar:
```java
if (!participation.getStatus().equals(participation.getStatus())) {
    // A validação real é feita na criação da Proof
}
```

Por:
```java
if (participation.getStatus() != ParticipationStatus.ACCEPTED) {
    throw new IllegalStateException(
        "Só é possível submeter prova em participação aceita. Status atual: "
        + participation.getStatus()
    );
}
```

### 1.2 `PUT /api/v1/challenges` sem identificar o challenge

Arquivo: `entrypoint/controllers/ChallengeController.java:126`

Trocar o path para `@PutMapping("/{id}")` e adicionar `@PathVariable Long id`. Ajustar `UpdateChallengeUseCase.execute` para receber o `id` como primeiro parâmetro e buscar o challenge existente (lançar `NoSuchElementException` se não achar). Ajustar também a assinatura de `UpdateChallengeRequest` (criar novo DTO; não reutilizar `CreateChallengeRequest`).

### 1.3 `GET /api/v1/challenges/{id}` é placeholder

Arquivo: `entrypoint/controllers/ChallengeController.java:67`

Implementar de verdade: criar `GetChallengeByIdUseCase` em `application/usecases/challenge/`, adicionar `@Bean` no `UseCaseConfig`, adicionar método `findById(Long id)` em `ChallengeRepository` (interface) + `ChallengeRepositoryImpl`. Retornar `ResponseEntity<ChallengeResponse>`.

### 1.4 `@NotBlank` em tipos não-String

Arquivos:
- `entrypoint/dtos/request/CreateParticipationRequest.java`
- `entrypoint/dtos/request/UpdateParticipationChallengeRequest.java`

Trocar todos `@NotBlank` aplicados a `Long`/`enum`/`User`/`Challenge` por `@NotNull`. (A refatoração completa desses DTOs — trocar objetos por IDs — é o Bloco 2; aqui só o mínimo pra não dar `ConstraintDeclarationException`.)

### 1.5 Bug de mapeamento em `ChallengeParticipationEntity.level`

Arquivo: `infrastructure/persistence/entities/ChallengeParticipationEntity.java:38-39`

O campo `level: String` está mapeado para `@Column(name = "created_at", nullable = false, updatable = false)`. Está errado — o nome semântico é `level`, a coluna diz `created_at`. Como o campo `level` não é usado de forma útil hoje, e o Bloco 5 vai reestruturar a participação para ter `progress`, aqui o correto é **renomear a coluna para `level`** e tornar nullable:

```java
@Column(name = "level", nullable = true)
private String level;
```

### 1.6 `ProofEntity.approved` nullable

Arquivo: `infrastructure/persistence/entities/ProofEntity.java`

Trocar `Boolean approved` por `boolean approved` primitivo, com `@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")`. Ajustar `Proof` domínio pra também usar primitivo.

### 1.7 `POST /challenges/{id}/invite` retornando String

Arquivo: `entrypoint/controllers/ChallengeController.java:76`

Trocar retorno de `ResponseEntity<String>` para `ResponseEntity<ChallengeParticipationResponse>` com status 201 CREATED. Use case `InviteUserToChallengeUseCase` já cria a participação — basta mapear e retornar.

**Critério de aceitação:**

- `./mvnw compile` passa.
- `./mvnw test` passa (os testes existentes, se houver; se não houver, ok).
- Chamada manual `POST /api/v1/participations/1/proof` em participação com status `INVITED` deve retornar 400 com mensagem de erro (teste com Postman/httpie).
- `GET /api/v1/challenges/1` retorna JSON do challenge, não string.
- `PUT /api/v1/challenges/1?creatorId=1` funciona (`creatorId` sairá em Bloco 3).

**Prompt para o plugin:**

```
Faça o Bloco 1 do PLANO_BACKEND_EUDUVIDO.md: correção dos 7 bugs críticos listados (1.1 a 1.7). Para cada um:
1. SubmitProofUseCase: trocar a comparação consigo mesmo por validação real de ParticipationStatus.ACCEPTED.
2. ChallengeController PUT: mover para /{id} com @PathVariable; adaptar UpdateChallengeUseCase para receber id.
3. GET /challenges/{id}: criar GetChallengeByIdUseCase de verdade; adicionar @Bean no UseCaseConfig; adicionar findById na ChallengeRepository (domain + impl).
4. DTOs CreateParticipationRequest e UpdateParticipationChallengeRequest: trocar @NotBlank por @NotNull onde o tipo não é String (aqui é apenas o mínimo — a refatoração completa é Bloco 2).
5. ChallengeParticipationEntity.level: mudar @Column(name="created_at") para @Column(name="level", nullable=true). Ajustar Flyway migration adicionando V2__fix_participation_level_column.sql com ALTER TABLE.
6. ProofEntity.approved: trocar Boolean por boolean primitivo; ajustar Proof domain; migration V3.
7. POST /challenges/{id}/invite: retornar ChallengeParticipationResponse com 201.
Ao final, rode ./mvnw compile e ./mvnw test, e me reporte.
```

---

## Bloco 2 — Refatoração dos DTOs (IDs em vez de entidades)

**Objetivo:** remover entidades de domínio dos DTOs de request; usar apenas IDs e tipos primitivos. Isso previne mass assignment e alinha com Clean Architecture (DTO é fronteira, não deve conhecer domínio).

**Arquivos afetados:**

- `entrypoint/dtos/request/CreateParticipationRequest.java`
- `entrypoint/dtos/request/UpdateParticipationChallengeRequest.java` → renomear para `UpdateParticipationRequest.java`
- `entrypoint/controllers/ParticipationController.java`
- `application/usecases/participation/CreateChallengeParticipationUseCase.java`
- `application/usecases/participation/UpdateChallengeParticipationUseCase.java`
- `config/UseCaseConfig.java`

**Passos:**

1. Reescrever `CreateParticipationRequest` como:
   ```java
   @Data @NoArgsConstructor @AllArgsConstructor
   public class CreateParticipationRequest {
       @NotNull(message = "userId é obrigatório")
       private Long userId;
       @NotNull(message = "challengeId é obrigatório")
       private Long challengeId;
   }
   ```

2. Reescrever `UpdateParticipationRequest`:
   ```java
   @Data @NoArgsConstructor @AllArgsConstructor
   public class UpdateParticipationRequest {
       @NotNull private Long id;
       @NotNull private ParticipationStatus status;
   }
   ```

3. `CreateChallengeParticipationUseCase.execute(Long userId, Long challengeId)`: busca `User` e `Challenge` nos respectivos repositórios (lança `NoSuchElementException` se não achar), aí chama `ChallengeParticipation.create(user, challenge)` e persiste.

4. `UpdateChallengeParticipationUseCase.execute(Long id, ParticipationStatus newStatus)`: busca participação; faz transição conforme método do domínio (`accept()`, `refuse()`, `complete()`) baseado no `newStatus` — não aceitar qualquer combinação, só as transições legais.

5. Atualizar `ParticipationController` para repassar IDs.

**Critério de aceitação:**

- Nenhum DTO em `entrypoint/dtos/request/` importa classes de `domain/entities/`.
- `./mvnw compile` passa.
- POST `/api/v1/participations` com body `{"userId": 1, "challengeId": 2}` cria participação.
- PUT `/api/v1/participations` com body `{"id": 1, "status": "ACCEPTED"}` atualiza.

**Prompt para o plugin:**

```
Faça o Bloco 2 do PLANO_BACKEND_EUDUVIDO.md: reescrever CreateParticipationRequest e UpdateParticipationRequest (renomear este) para usar apenas IDs e tipos primitivos; adaptar os use cases correspondentes pra buscar User e Challenge pelos repositórios; atualizar o controller. Garantir que nenhum arquivo em entrypoint/dtos/request/ importe de domain/entities. Rode ./mvnw compile ao final.
```

---

## Bloco 3 — Segurança: BCrypt, JWT, SecurityFilterChain, AuthController

**Objetivo:** implementar autenticação real. Senhas com BCrypt, login emitindo JWT (HS256, 24h), endpoints protegidos exceto `/auth/**` e `POST /users`.

**Decisões arquiteturais:**

- Biblioteca JWT: `io.jsonwebtoken:jjwt-api` + `jjwt-impl` + `jjwt-jackson` (versão 0.12.x).
- Token em header `Authorization: Bearer <jwt>`.
- Claims do JWT: `sub` (userId), `email`, `name`, `iat`, `exp`.
- **Sem refresh token** nessa primeira versão (simplificação consciente; quando expirar, front faz login de novo).
- `PasswordEncoder` como `@Bean` no `config/SecurityConfig`.
- Filter JWT como classe em `infrastructure/security/JwtAuthenticationFilter.java`.
- `JwtService` em `infrastructure/security/JwtService.java` (gera e valida token).
- `AuthUser` em `infrastructure/security/AuthUser.java` (implementa `UserDetails`, wrapping da entidade User).
- `AuthController` em `entrypoint/controllers/AuthController.java` com `POST /api/v1/auth/login`.
- Fazer `CreateUserUseCase` encriptar senha (injetar `PasswordEncoder`).
- Criar `LoginUseCase` em `application/usecases/auth/`.

**Arquivos novos:**

- `config/SecurityConfig.java`
- `infrastructure/security/JwtService.java`
- `infrastructure/security/JwtAuthenticationFilter.java`
- `infrastructure/security/AuthUser.java`
- `application/usecases/auth/LoginUseCase.java`
- `entrypoint/controllers/AuthController.java`
- `entrypoint/dtos/request/LoginRequest.java`
- `entrypoint/dtos/response/LoginResponse.java` (contém `token`, `expiresAt`, `user: UserResponse`)

**Arquivos alterados:**

- `pom.xml` — adicionar `spring-boot-starter-security` e `jjwt`.
- `application.yml` — já tem `app.security.jwt.secret` e `expiration-minutes` do Bloco 0.
- `CreateUserUseCase.java` — injetar `PasswordEncoder`, encriptar antes de salvar.
- `UserRepository.java` (domain) — adicionar `Optional<User> findByEmail(String email)`.
- `UserRepositoryImpl.java` — implementar.
- `UserJpaRepository.java` — adicionar `Optional<UserEntity> findByEmail(String email)`.
- `UseCaseConfig.java` — atualizar `CreateUserUseCase` bean para receber encoder; adicionar `LoginUseCase` bean.
- Todos os controllers que usam `@RequestParam creatorId` / `userId` — trocar por `@AuthenticationPrincipal AuthUser principal` e usar `principal.getId()`.

**SecurityConfig (template):**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/v1/files/**").permitAll()  // serve mídia; pode restringir depois
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

**JwtService (esqueleto):**

```java
@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
                      @Value("${app.security.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(String.valueOf(user.getId()))
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
```

**Critério de aceitação:**

- `POST /api/v1/auth/login` com credenciais corretas retorna 200 + `{ token, expiresAt, user }`.
- Credenciais erradas → 401.
- `GET /api/v1/users` sem token → 401.
- `GET /api/v1/users` com header `Authorization: Bearer <jwt válido>` → 200.
- `POST /api/v1/users` sem token → 201 (cadastro público).
- Senha persistida no MySQL começa com `$2a$` ou `$2b$` (bcrypt).
- `POST /api/v1/challenges` lê `creatorId` de `@AuthenticationPrincipal`, **não** de `@RequestParam` — query param sumiu.

**Prompt para o plugin:**

```
Faça o Bloco 3 do PLANO_BACKEND_EUDUVIDO.md: implementar autenticação JWT.
- Adicione spring-boot-starter-security e jjwt (api/impl/jackson 0.12.x) no pom.xml.
- Crie config/SecurityConfig com PasswordEncoder(BCrypt) e SecurityFilterChain stateless seguindo o template do documento.
- Crie infrastructure/security/: JwtService, JwtAuthenticationFilter, AuthUser(implements UserDetails).
- Adicione findByEmail em UserRepository (domain), UserRepositoryImpl, UserJpaRepository.
- Crie application/usecases/auth/LoginUseCase.
- Crie entrypoint/controllers/AuthController com POST /api/v1/auth/login recebendo LoginRequest{email,password} e retornando LoginResponse{token,expiresAt,user:UserResponse}.
- Atualize CreateUserUseCase para encriptar senha via PasswordEncoder (injetar; atualizar @Bean em UseCaseConfig).
- Substitua TODOS os @RequestParam creatorId/userId nos controllers por @AuthenticationPrincipal AuthUser principal; use principal.getId(). Ajuste assinaturas dos use cases se necessário.
- Ao final, rode ./mvnw compile e me mostre o fluxo de auth funcionando via curl (login → token → request protegido).
```

---

## Bloco 4 — Limpeza: remover entidade Invite (consolidação em ChallengeParticipation)

**Objetivo:** eliminar a duplicação de conceito. O modelo `Invite` some; o fluxo "convidar alguém pra um desafio" usa `ChallengeParticipation` com status `INVITED`.

**Arquivos a deletar:**

- `domain/entities/Invite.java`
- `domain/repositories/InviteRepository.java`
- `infrastructure/persistence/entities/InviteEntity.java`
- `infrastructure/persistence/repositories/InviteJpaRepository.java`
- `infrastructure/repositories/InviteRepositoryImpl.java`
- `application/usecases/invite/` (pasta inteira: `AcceptInviteUseCase`, `CreateInviteUseCase`, `DeleteInviteUseCase`, `GetInviteUseCase`, `ListInvitesUseCase`)
- `entrypoint/controllers/InviteController.java`
- `entrypoint/dtos/request/CreateInviteRequest.java`
- `entrypoint/dtos/response/InviteResponse.java`

**Arquivos alterados:**

- `config/UseCaseConfig.java` — remover todos os beans de `*InviteUseCase`.

**Arquivos a criar:**

- Migration `V4__drop_invites_table.sql`:
  ```sql
  DROP TABLE IF EXISTS invites;
  ```

- Novos endpoints (em `ParticipationController`, se ainda não existirem):
  - `GET /api/v1/participations/received` — lista participações onde `user` é o usuário logado e `status = INVITED` (usa `ListReceivedChallengesUseCase` existente, filtrando por status).
  - `GET /api/v1/participations/sent` — lista participações onde `challenge.creator` é o usuário logado e `status = INVITED`.

**Critério de aceitação:**

- Nenhuma referência a `Invite` em qualquer arquivo do projeto (`grep -r "Invite" src/main` retorna vazio).
- `./mvnw compile` passa.
- Flyway aplicou `V4`; tabela `invites` não existe mais.
- Endpoint `GET /api/v1/participations/received` funciona.
- Endpoint antigo `POST /challenges/{id}/invite` continua funcionando (ele já cria `ChallengeParticipation` com `INVITED` — é o fluxo correto agora).

**Prompt para o plugin:**

```
Faça o Bloco 4 do PLANO_BACKEND_EUDUVIDO.md: remover a entidade Invite e consolidar tudo em ChallengeParticipation com status INVITED.
- Delete os arquivos listados no documento (entidade, repositório, use cases, controller, DTOs).
- Remova os @Bean correspondentes de UseCaseConfig.
- Crie migration V4__drop_invites_table.sql.
- Adicione endpoints GET /api/v1/participations/received e /sent em ParticipationController (filtrando por status=INVITED).
- Verifique com grep que não sobrou nenhuma referência a Invite em src/main.
- Rode ./mvnw compile.
```

---

## Bloco 5 — Domínio acadêmico: difficulty, subject, goal, progress

**Objetivo:** trazer o código para o domínio que a documentação descreve — desafios **acadêmicos** com dificuldade, disciplina, meta mensurável e progresso.

**Novos enums em `domain/enums/`:**

- `Difficulty` — `EASY`, `MEDIUM`, `HARD`.
- `GoalType` — `HOURS`, `PAGES`, `EXERCISES`, `SESSIONS`, `CUSTOM`.

**Alterações em `Challenge` (domínio):**

- Transformar o campo existente `String difficulty` em `Difficulty difficulty` (enum).
- Adicionar `String subject` (disciplina: "Cálculo I", "Banco de Dados", etc.).
- Adicionar `GoalType goalType`.
- Adicionar `Integer goalValue` (ex: 10 horas, 50 páginas).
- Remover o campo `Double progress` de `Challenge` — progresso é **por participação**, não por desafio (decisão de modelagem correta).
- Atualizar `Challenge.create(...)`, `createFromDatabase(...)`, `validateChallengeData(...)` e o construtor.

**Alterações em `ChallengeParticipation` (domínio):**

- Adicionar `Integer progress` (0 a `challenge.goalValue`).
- Adicionar método `updateProgress(Integer newProgress)` validando: não negativo; não maior que `goalValue`; só permitido se status `ACCEPTED`.
- Remover o campo `String level` (morrendo sem substituição — não está no domínio documentado).

**Alterações em `ChallengeEntity` e `ChallengeParticipationEntity`:**

- Espelhar as mudanças acima.
- `@Enumerated(EnumType.STRING)` nos novos enums.

**Migration `V5__academic_domain.sql`:**

```sql
ALTER TABLE challenges
    DROP COLUMN IF EXISTS progress,
    ADD COLUMN subject VARCHAR(100),
    ADD COLUMN goal_type VARCHAR(20),
    ADD COLUMN goal_value INT,
    MODIFY COLUMN difficulty VARCHAR(10);

UPDATE challenges SET difficulty = 'MEDIUM' WHERE difficulty IS NULL OR difficulty = '';

ALTER TABLE challenge_participations
    DROP COLUMN IF EXISTS level,
    ADD COLUMN progress INT NOT NULL DEFAULT 0;
```

**DTOs novos/alterados:**

- `CreateChallengeRequest` — adicionar `difficulty`, `subject`, `goalType`, `goalValue` (todos validados).
- `UpdateChallengeRequest` (novo) — mesmos campos opcionais.
- `ChallengeResponse` — expor `difficulty`, `subject`, `goalType`, `goalValue`.
- `ChallengeParticipationResponse` — expor `progress` (e remover `level`).
- `UpdateProgressRequest` (novo) — `@NotNull @Min(0) Integer progress`.

**Endpoints novos:**

- `PATCH /api/v1/participations/{id}/progress` — body `{progress: 5}`. Usa `UpdateProgressUseCase`. Só o próprio participante pode atualizar.
- `POST /api/v1/proofs/{id}/reject` — par do `approve`. Use case `RejectProofUseCase`. Usuário autenticado deve ser o criador do challenge (ou moderador no futuro). Adicionar campo `String rejectionReason` em `Proof` (+ migration).

**Critério de aceitação:**

- `Challenge` e `ChallengeParticipation` do domínio têm os novos campos.
- `POST /api/v1/challenges` com `{"title": "Estudar Cálculo", "description": "Capítulo 3", "difficulty": "HARD", "subject": "Cálculo I", "goalType": "PAGES", "goalValue": 50, "deadline": "2026-05-01T00:00:00", "locationRequired": false}` cria challenge.
- `PATCH /api/v1/participations/1/progress` com `{"progress": 10}` atualiza.
- `POST /api/v1/proofs/5/reject` com body `{"reason": "Foto borrada"}` rejeita.
- Tentar `progress` maior que `goalValue` → 400.

**Prompt para o plugin:**

```
Faça o Bloco 5 do PLANO_BACKEND_EUDUVIDO.md: trazer o domínio acadêmico para o código.
- Crie enums Difficulty (EASY/MEDIUM/HARD) e GoalType (HOURS/PAGES/EXERCISES/SESSIONS/CUSTOM) em domain/enums.
- Atualize Challenge (domain) conforme o doc: difficulty vira enum, adicione subject/goalType/goalValue, remova progress.
- Atualize ChallengeParticipation (domain): adicione progress (Integer) com método updateProgress() validando; remova level.
- Espelhe em ChallengeEntity e ChallengeParticipationEntity.
- Crie migration V5__academic_domain.sql seguindo o SQL do documento.
- Atualize DTOs: CreateChallengeRequest, UpdateChallengeRequest (novo), ChallengeResponse, ChallengeParticipationResponse.
- Crie UpdateProgressUseCase + RejectProofUseCase + DTOs e endpoints:
  - PATCH /api/v1/participations/{id}/progress
  - POST  /api/v1/proofs/{id}/reject
- Adicione campo rejectionReason em Proof e ProofEntity + migration.
- Registre todos os novos @Bean em UseCaseConfig.
- Rode ./mvnw compile e verifique.
```

---

## Bloco 6 — Expiração automática de desafios (@Scheduled)

**Objetivo:** cumprir RF10. Challenges com `deadline < now` e status `PENDING`/`ACTIVE` devem virar `EXPIRED` automaticamente.

**Arquivos novos:**

- `infrastructure/scheduling/ExpireChallengesJob.java`:
  ```java
  @Component
  public class ExpireChallengesJob {
      private final UpdateExpiredChallengesUseCase useCase;
      public ExpireChallengesJob(UpdateExpiredChallengesUseCase useCase) { this.useCase = useCase; }

      @Scheduled(cron = "0 */5 * * * *")  // a cada 5 minutos
      public void run() { useCase.execute(); }
  }
  ```

**Arquivos alterados:**

- `EuDuvidoApiApplication.java` — adicionar `@EnableScheduling`.
- Garantir que `UpdateExpiredChallengesUseCase.execute()` faz a lógica correta: busca todos challenges com `deadline < now` e status ≠ COMPLETED/EXPIRED, muda para EXPIRED, salva.

**Critério de aceitação:**

- Log "Expirou X desafios" a cada 5 minutos (colocar log no use case).
- Desafio com `deadline` no passado criado via POST tem seu status virado para `EXPIRED` em até 5 min.

**Prompt para o plugin:**

```
Faça o Bloco 6 do PLANO_BACKEND_EUDUVIDO.md: expiração automática de desafios.
- Adicione @EnableScheduling em EuDuvidoApiApplication.
- Crie infrastructure/scheduling/ExpireChallengesJob com @Scheduled(cron="0 */5 * * * *") chamando UpdateExpiredChallengesUseCase.
- Revise UpdateExpiredChallengesUseCase para de fato buscar desafios expirados (deadline < now, status PENDING ou ACTIVE) e mudar para EXPIRED, com log informativo.
- Adicione método findExpiredCandidates no ChallengeRepository se necessário.
- Rode ./mvnw compile.
```

---

## Bloco 7 — Filtros por status e paginação

**Objetivo:** cumprir RF12 (filtragem por status) e RNF04 (escalabilidade).

**Alterações:**

- `GET /api/v1/challenges?status=ACTIVE&page=0&size=20&sort=createdAt,desc` — aceitar `Optional<ChallengeStatus> status` + `Pageable pageable`.
- `GET /api/v1/participations/user/{userId}?status=INVITED` — idem.
- `GET /api/v1/users?page=0&size=20` — paginar.
- `ListChallengeUseCase.execute(ChallengeStatus statusOrNull, Pageable pageable): Page<Challenge>`.
- `ChallengeRepository.findAll(ChallengeStatus statusOrNull, Pageable pageable)`.
- Criar `PageResponse<T>` genérico em `entrypoint/dtos/response/` para não vazar `org.springframework.data.domain.Page` direto no JSON (mais limpo).

**Critério de aceitação:**

- `GET /api/v1/challenges?status=ACTIVE&size=5` retorna no máximo 5 itens com status ACTIVE.
- `GET /api/v1/challenges` sem params retorna paginado com `page=0, size=20` por padrão.
- Response inclui `content`, `page`, `size`, `totalElements`, `totalPages`.

**Prompt para o plugin:**

```
Faça o Bloco 7 do PLANO_BACKEND_EUDUVIDO.md: paginação e filtros.
- Adapte ListChallengeUseCase e ListReceivedChallengesUseCase para aceitar Optional<Status> e Pageable e retornar Page<Domain>.
- Atualize os @Bean em UseCaseConfig.
- Adapte ChallengeRepository (domain) + Impl para suportar filtro + Pageable.
- Crie PageResponse<T> genérico em entrypoint/dtos/response.
- Atualize endpoints: GET /challenges, GET /participations/user/{userId}, GET /users — todos aceitam Pageable e status opcional onde fizer sentido.
- Rode ./mvnw compile e teste com curl.
```

---

## Bloco 8 — Storage local e upload multipart de mídia

**Objetivo:** cumprir RNF03 (storage externo) e permitir upload real de provas.

**Decisões:**

- Disco local em `./uploads/` (configurável via `app.storage.local.path`).
- Nome do arquivo: `{uuid}.{ext}` — não usar nome original (evita path traversal).
- Endpoint `POST /api/v1/files` — multipart, retorna `{ filename, url }`.
- Endpoint `GET /api/v1/files/{filename}` — serve o arquivo com Content-Type correto.
- `SubmitProofRequest` passa a ter duas formas:
  - **Forma A (recomendada)**: endpoint multipart `POST /api/v1/participations/{id}/proof` com `file`, `mediaType`, `latitude`, `longitude` — o próprio endpoint sobe o arquivo e cria a proof.
  - **Forma B (legada)**: manter endpoint atual que aceita `mediaUrl` (útil se front fizer upload separado). Mas priorizar A.

**Arquivos novos:**

- `application/services/FileStorageService.java` (interface no domínio funcional da aplicação):
  ```java
  public interface FileStorageService {
      StoredFile store(byte[] content, String originalFilename, String contentType);
      byte[] retrieve(String identifier);
      void delete(String identifier);
  }
  ```
- `application/services/StoredFile.java` (record com `identifier`, `publicUrl`, `contentType`, `sizeBytes`).
- `infrastructure/storage/LocalDiskFileStorageService.java` — implementação com `@Component`, lê `${app.storage.local.path}`, cria diretório se não existir.
- `entrypoint/controllers/FileController.java` — `POST /api/v1/files` (autenticado) e `GET /api/v1/files/{filename}` (público).
- `entrypoint/dtos/response/UploadResponse.java`.

**Arquivos alterados:**

- `SubmitProofUseCase.execute(Long participationId, byte[] fileContent, String filename, String contentType, MediaType mediaType, Double lat, Double lng)` — usa `FileStorageService` para guardar, depois cria `Proof` com `mediaUrl = storedFile.publicUrl`.
- `ParticipationController.submitProof` vira `@PostMapping(value = "/{id}/proof", consumes = MULTIPART_FORM_DATA_VALUE)`.
- `application.yml` — `app.storage.local.path` (já adicionado no Bloco 0).

**Critério de aceitação:**

- `curl -F file=@foto.jpg -F mediaType=PHOTO -F latitude=-23.5 -F longitude=-46.6 -H "Authorization: Bearer $TOKEN" localhost:8080/api/v1/participations/1/proof` cria a prova.
- Arquivo aparece em `./uploads/{uuid}.jpg`.
- `GET /api/v1/files/{uuid}.jpg` retorna a imagem com content-type correto.
- Tentativa de `../` no path → 400.

**Prompt para o plugin:**

```
Faça o Bloco 8 do PLANO_BACKEND_EUDUVIDO.md: storage local + upload multipart.
- Crie a interface FileStorageService em application/services e o record StoredFile.
- Implemente LocalDiskFileStorageService em infrastructure/storage (lê app.storage.local.path; cria dir; nome do arquivo = UUID + extensão; bloquear path traversal).
- Crie FileController com POST /api/v1/files (multipart, autenticado) e GET /api/v1/files/{filename} (público — já liberado no SecurityConfig do Bloco 3).
- Altere SubmitProofUseCase para aceitar byte[] + metadata e usar o storage.
- Altere o endpoint de submit proof para multipart (consumes=MULTIPART_FORM_DATA_VALUE), aceitando MultipartFile file + MediaType + lat/lng.
- Rode ./mvnw compile e teste um upload com curl.
```

---

## Bloco 9 — Validação por IA (Claude)

**Objetivo:** implementar os dois validadores descritos nos prompts do `Documentação APP.docx`:

1. Validar que um desafio é acadêmico antes de criar.
2. Validar que uma prova (imagem) tem contexto de estudo antes de salvar.

**Decisões arquiteturais (Clean):**

- Interface `AiValidationService` em `application/services/`:
  ```java
  public interface AiValidationService {
      ValidationResult validateChallenge(String title, String description);
      ValidationResult validateProofImage(byte[] imageBytes, String contentType);
      ValidationResult validateProofLocation(String locationType);
  }
  public record ValidationResult(boolean valid, double confidence, String reason, List<String> errors) {}
  ```
- Implementação em `infrastructure/ai/AnthropicValidationService.java` — usa Spring WebClient (adicionar `spring-boot-starter-webflux` como dependência só pra WebClient).
- Os prompts ficam em `infrastructure/ai/prompts/` como arquivos `.txt` (copiar **literalmente** os dois prompts do docx), carregados via `ClassPathResource`.
- Modelo: `claude-sonnet-4-6` (config `app.anthropic.model`).
- Para validação de imagem: usar bloco `type: "image"` da API Messages do Claude com `source.type: base64`.
- Key vem de `${app.anthropic.api-key}`. Se vazia, serviço devolve `ValidationResult(true, 0, "IA desabilitada", List.of())` — fallback seguro para desenvolvimento sem key.

**Fluxo de integração:**

- `CreateChallengeUseCase.execute(...)` chama `aiService.validateChallenge(title, description)`. Se `!valid`, lança `AiValidationException(motivo, erros)`. Handler retorna 422 Unprocessable Entity.
- `SubmitProofUseCase.execute(...)` (após Bloco 8): chama `aiService.validateProofImage(bytes, contentType)`. Se `!valid`, **persiste a prova mesmo assim** mas com `aiValid=false`, `aiReason=...`, e marca `approved=false` (não bloqueia — dá transparência ao usuário). Criador decide se aprova manualmente.

**Arquivos novos:**

- `application/services/AiValidationService.java`
- `application/services/ValidationResult.java`
- `application/exception/AiValidationException.java`
- `infrastructure/ai/AnthropicValidationService.java`
- `infrastructure/ai/prompts/validate_challenge.txt` (copiar literal do docx)
- `infrastructure/ai/prompts/validate_proof.txt` (copiar literal do docx)
- `entrypoint/dtos/response/AiValidationResponse.java` (sub-objeto do `ProofResponse`)

**Arquivos alterados:**

- `pom.xml` — adicionar `spring-boot-starter-webflux`.
- `domain/entities/Proof.java` — adicionar campos `Boolean aiValid`, `Double aiConfidence`, `String aiReason`.
- `ProofEntity.java` — mesmos campos + migration V6.
- `UseCaseConfig.java` — atualizar CreateChallengeUseCase e SubmitProofUseCase para receber `AiValidationService`.
- `GlobalExceptionHandler` — handler pra `AiValidationException` → 422.

**Esqueleto do `AnthropicValidationService`:**

```java
@Component
public class AnthropicValidationService implements AiValidationService {
    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final String challengePrompt;
    private final String proofPrompt;
    private final ObjectMapper objectMapper;

    public AnthropicValidationService(
            @Value("${app.anthropic.api-key:}") String apiKey,
            @Value("${app.anthropic.base-url}") String baseUrl,
            @Value("${app.anthropic.model}") String model,
            ObjectMapper objectMapper,
            ResourceLoader loader) throws IOException {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("anthropic-version", "2023-06-01")
            .build();
        this.challengePrompt = loadPrompt(loader, "classpath:prompts/validate_challenge.txt");
        this.proofPrompt = loadPrompt(loader, "classpath:prompts/validate_proof.txt");
    }

    @Override
    public ValidationResult validateChallenge(String title, String description) {
        if (apiKey == null || apiKey.isBlank()) {
            return new ValidationResult(true, 0, "IA desabilitada (sem API key)", List.of());
        }
        String prompt = challengePrompt
            .replace("{titulo}", title)
            .replace("{descricao}", description);
        // chamar /messages, parsear JSON retornado, montar ValidationResult
        // ...
    }
    // validateProofImage usa bloco image+base64; validateProofLocation envia só texto do tipo de local
}
```

**Critério de aceitação:**

- Com `ANTHROPIC_API_KEY` configurada:
  - `POST /challenges` com `{"title": "Ir à academia 3x por semana"}` → 422 com motivo do prompt.
  - `POST /challenges` com `{"title": "Ler 10 páginas de Clean Code"}` → 201.
- Sem API key: endpoints funcionam normalmente (fallback permissivo, com log "AI validation disabled").
- `ProofResponse` inclui `aiValidation: { valid, confidence, reason }`.

**Prompt para o plugin:**

```
Faça o Bloco 9 do PLANO_BACKEND_EUDUVIDO.md: integrar Claude para validação.
- Adicione spring-boot-starter-webflux no pom.xml.
- Crie application/services/AiValidationService (interface), ValidationResult (record), application/exception/AiValidationException.
- Crie infrastructure/ai/AnthropicValidationService que:
  * Usa WebClient contra app.anthropic.base-url com header anthropic-version=2023-06-01 e x-api-key.
  * Lê modelo de app.anthropic.model.
  * Se api-key vazia, sempre retorna ValidationResult(true, 0, "IA desabilitada", []) — fallback seguro.
  * Implementa validateChallenge, validateProofImage (base64 bloco image) e validateProofLocation.
- Copie os dois prompts do Documentação APP.docx (estão no final do doc — validação de desafio e validação de evidência) como arquivos em src/main/resources/prompts/validate_challenge.txt e validate_proof.txt.
- Integre no CreateChallengeUseCase: se !valid → AiValidationException.
- Integre no SubmitProofUseCase (pós Bloco 8): não bloqueia, apenas preenche campos aiValid/aiConfidence/aiReason na Proof.
- Adicione campos aiValid/aiConfidence/aiReason em Proof domain e ProofEntity; migration V6.
- Adicione handler de AiValidationException em GlobalExceptionHandler → 422 com ErrorResponse contendo motivo + erros.
- Atualize @Bean em UseCaseConfig.
- Rode ./mvnw compile.
```

---

## Bloco 10 — Testes unitários e de integração

**Objetivo:** ter cobertura básica dos use cases e dos controllers.

**Escopo mínimo:**

- `@DataJpaTest` em cada `*RepositoryImpl` (pelo menos um teste por método).
- `@WebMvcTest` em cada controller (com `MockMvc` + mock dos use cases).
- Testes unitários puros dos use cases mais complexos (`SubmitProofUseCase`, `UpdateExpiredChallengesUseCase`, `UpdateProgressUseCase`).
- Testes das regras de domínio (`Challenge.create` valida deadline, `ChallengeParticipation.accept` só em INVITED, etc.).

**Ferramentas:**

- JUnit 5 (já vem no starter-test).
- Mockito (idem).
- AssertJ (idem).
- Testcontainers para `@DataJpaTest` com MySQL real:
  ```xml
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>mysql</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
  </dependency>
  ```

**Estrutura:**

```
src/test/java/com/euduvido/euduvido_api/
├── domain/
│   ├── ChallengeTest.java
│   ├── ChallengeParticipationTest.java
│   └── ProofTest.java
├── application/usecases/
│   ├── challenge/CreateChallengeUseCaseTest.java
│   ├── proof/SubmitProofUseCaseTest.java
│   └── ...
├── infrastructure/persistence/
│   └── ChallengeRepositoryImplTest.java (com Testcontainers)
└── entrypoint/controllers/
    ├── AuthControllerTest.java (@WebMvcTest)
    ├── ChallengeControllerTest.java
    └── ...
```

**Critério de aceitação:**

- `./mvnw test` passa com pelo menos 30 testes.
- Cobertura domínio > 80% (via JaCoCo — adicionar plugin no `pom.xml`).

**Prompt para o plugin:**

```
Faça o Bloco 10 do PLANO_BACKEND_EUDUVIDO.md: testes.
- Adicione Testcontainers (mysql + junit-jupiter) no pom.xml test scope.
- Adicione plugin jacoco-maven no pom.
- Crie testes de domínio (Challenge, ChallengeParticipation, Proof) cobrindo create/validate/transitions.
- Crie testes unitários com Mockito dos use cases complexos (SubmitProofUseCase, CreateChallengeUseCase, UpdateProgressUseCase, UpdateExpiredChallengesUseCase).
- Crie testes de controller com @WebMvcTest para AuthController, ChallengeController, ParticipationController.
- Crie 1 teste @DataJpaTest com Testcontainers validando ChallengeRepositoryImpl.
- Rode ./mvnw test e me mostre contagem + jacoco.
```

---

## Bloco 11 — OpenAPI/Swagger

**Objetivo:** expor contrato da API para o frontend gerar clientes tipados.

**Arquivos alterados:**

- `pom.xml` — `org.springdoc:springdoc-openapi-starter-webmvc-ui` (versão compatível com Spring Boot 4.x; checar na Maven Central).
- `application.yml` — `springdoc.swagger-ui.path=/swagger-ui.html`.
- `SecurityConfig` — já libera `/v3/api-docs/**` e `/swagger-ui/**` (Bloco 3).
- Anotar controllers com `@Tag(name = "...")` e cada método com `@Operation(summary = "...")`.
- Expor esquema de segurança JWT: `@SecurityScheme(name = "bearerAuth", type = HTTP, scheme = "bearer", bearerFormat = "JWT")` na classe principal.

**Critério de aceitação:**

- `http://localhost:8080/swagger-ui.html` abre com todos os endpoints listados, agrupados por tag.
- Botão "Authorize" permite colar o JWT.
- `http://localhost:8080/v3/api-docs` retorna JSON válido do OpenAPI 3.

**Prompt para o plugin:**

```
Faça o Bloco 11 do PLANO_BACKEND_EUDUVIDO.md: Springdoc/OpenAPI.
- Adicione springdoc-openapi-starter-webmvc-ui compatível com Spring Boot 4.0.1.
- Configure swagger-ui em /swagger-ui.html.
- Adicione @SecurityScheme JWT na classe principal.
- Anote cada controller com @Tag e cada método com @Operation + @ApiResponses.
- Rode a aplicação e verifique /swagger-ui.html.
```

---

## Bloco 12 — Polimento: exceções, logs, CORS, README

**Objetivo:** fechar as arestas.

**Exceções:**

- `GlobalExceptionHandler` cobrindo:
  - `MethodArgumentNotValidException` → 400 com lista de campos inválidos.
  - `NoSuchElementException`/`EntityNotFoundException` → 404.
  - `IllegalArgumentException` → 400.
  - `IllegalStateException` → 409 (conflito de estado — ex: completar desafio não aceito).
  - `AiValidationException` → 422.
  - `AccessDeniedException` → 403.
  - `BadCredentialsException` → 401.
  - `Exception` fallback → 500 (logando, nunca expondo stack trace em JSON).

**Logs:**

- Configurar log JSON estruturado (opcional) ou ao menos log pattern com request ID (MDC).
- Filtro `RequestIdFilter` que gera `X-Request-Id` se não vier e inclui no MDC.

**CORS:**

- Em `CorsConfig`, restringir `allowedOrigins` a `http://localhost:19006`, `http://localhost:8081` (Expo) e placeholder de prod. Remover `"*"`.

**README.md:**

Criar/atualizar `README.md` na raiz de `euduvido-api/`:

- Pré-requisitos (Java 21, Docker, Maven wrapper).
- `.env` — copiar de `.env.example`.
- `docker compose up -d mysql`
- `./mvnw spring-boot:run`
- Como rodar testes.
- Links: `/swagger-ui.html`, `/v3/api-docs`.
- Como obter API key da Anthropic.

**Critério de aceitação:**

- Erros retornam JSON consistente no formato `{ timestamp, status, code, message, errors?: [...] }`.
- CORS bloqueia origens fora da whitelist (testar com Postman + header Origin).
- README permite que uma pessoa nova clone o repo e rode em < 10 minutos.

**Prompt para o plugin:**

```
Faça o Bloco 12 do PLANO_BACKEND_EUDUVIDO.md: polimento final.
- Complete GlobalExceptionHandler com todos os handlers listados, usando um ErrorResponse uniforme { timestamp, status, code, message, errors }.
- Adicione RequestIdFilter (MDC) e ajuste log pattern.
- Restrinja CORS a localhost:19006, localhost:8081 e um placeholder de produção.
- Escreva README.md na raiz de euduvido-api conforme o esqueleto do documento.
- Rode ./mvnw test e ./mvnw spring-boot:run para confirmar que está tudo de pé.
```

---

## Apêndice A — Convenções de código

- **Pacote raiz:** `com.euduvido.euduvido_api`.
- **Camadas:**
  - `domain/` — POJOs e interfaces puras. Pode depender só de Java padrão (sem Spring, sem JPA, sem Lombok).
  - `application/usecases/<aggregate>/` — classes `*UseCase` com `execute(...)`. POJOs, sem anotação. Wiring em `UseCaseConfig`.
  - `application/services/` — interfaces (contratos) de serviços da camada de aplicação (`FileStorageService`, `AiValidationService`).
  - `application/exception/` — exceções da camada de aplicação.
  - `infrastructure/persistence/entities/` — entidades JPA (`*Entity`).
  - `infrastructure/persistence/repositories/` — interfaces Spring Data (`*JpaRepository`).
  - `infrastructure/repositories/` — implementações dos repositórios do domínio (`*RepositoryImpl`).
  - `infrastructure/security/` — JWT, filter, `UserDetails`.
  - `infrastructure/storage/` — `LocalDiskFileStorageService`.
  - `infrastructure/ai/` — `AnthropicValidationService`.
  - `infrastructure/scheduling/` — jobs agendados.
  - `entrypoint/controllers/` — `@RestController`.
  - `entrypoint/dtos/request/` e `entrypoint/dtos/response/` — DTOs anotados com Lombok (`@Data @NoArgsConstructor @AllArgsConstructor`) e Jakarta Validation.
  - `config/` — configurações Spring (`SecurityConfig`, `CorsConfig`, `UseCaseConfig`, `OpenApiConfig`).
  - `exception/` — `GlobalExceptionHandler` e `ErrorResponse`.

- **Nomenclatura:**
  - UseCases: verbo no infinitivo + substantivo (`CreateChallengeUseCase`).
  - DTOs request: `<Verbo><Entidade>Request` (`CreateChallengeRequest`).
  - DTOs response: `<Entidade>Response` (`ChallengeResponse`).
  - Métodos estáticos de fábrica no domínio: `create(...)` (novo), `createFromDatabase(...)` (hidrata).
  - Métodos de mapeamento JPA: `toDomain()`, `fromDomain(...)`.

- **Commits:** Conventional Commits. Ex: `fix(proof): validate ACCEPTED status before submit`, `feat(auth): JWT bearer authentication`.

- **Um Bean por use case** no `UseCaseConfig`. Nada de `@Component` em UseCase.

- **Nunca** retornar `List<Entity>` direto de Controller. Sempre mapear para `List<Response>` ou `PageResponse<Response>`.

- **Autenticação em endpoints:** usar `@AuthenticationPrincipal AuthUser principal`. Nunca `@RequestParam userId`.

---

## Apêndice B — Comandos úteis

```bash
# Primeira vez
cp .env.example .env
docker compose up -d mysql
./mvnw clean install

# Dia a dia
docker compose up -d mysql
./mvnw spring-boot:run

# Rodar testes
./mvnw test

# Cobertura (após Bloco 10)
./mvnw verify
# Abrir target/site/jacoco/index.html

# Reset do banco local (dev)
docker compose down -v
docker compose up -d mysql

# Testar endpoint protegido
TOKEN=$(curl -s -X POST localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"a@a.com","password":"123456"}' | jq -r .token)
curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/v1/challenges
```

---

## Apêndice C — Checklist final (rastreabilidade RF × bloco)

| RF/RNF | Descrição | Bloco responsável | Status esperado ao fim |
|---|---|---|---|
| RF01 | Cadastro de usuários | 3 (senha BCrypt) | ✅ |
| RF02 | Autenticação | 3 | ✅ |
| RF03 | Criar desafios | 1, 2, 5 (campos acadêmicos), 9 (validação IA) | ✅ |
| RF04 | Convidar outros | 4 (consolidação) | ✅ |
| RF05 | Aceitar/recusar | 2 | ✅ |
| RF06 | Registrar participantes | 2 | ✅ |
| RF07 | Atualizar progresso | 5 | ✅ |
| RF08 | Enviar prova | 5, 8, 9 | ✅ |
| RF09 | Aprovar/rejeitar prova | 5 (rejeição + motivo) | ✅ |
| RF10 | Status conforme prazo | 6 (scheduled) | ✅ |
| RF11 | Ver desafios criados/participados | 4, 7 | ✅ |
| RF12 | Filtragem por status | 7 | ✅ |
| RNF01 | Multiplataforma | N/A (front) | ✅ |
| RNF02 | Autenticação segura | 3 | ✅ |
| RNF03 | Storage externo | 8 | ✅ (disco local) |
| RNF04 | Escalabilidade | 0 (MySQL), 7 (paginação) | ✅ |
| RNF05 | UI responsiva | N/A (front) | — |

---

## Ordem de execução recomendada e tempo estimado

| Bloco | Esforço (solo + plugin) |
|---|---|
| 0 — Setup MySQL | 1-2h |
| 1 — Bugs críticos | 1-2h |
| 2 — DTOs com IDs | 1h |
| 3 — Segurança JWT | 3-4h |
| 4 — Remover Invite | 30min |
| 5 — Domínio acadêmico | 2-3h |
| 6 — Scheduled | 20min |
| 7 — Paginação | 1h |
| 8 — Storage + multipart | 2h |
| 9 — Claude | 2-3h |
| 10 — Testes | 3-4h |
| 11 — Swagger | 30min |
| 12 — Polimento | 1-2h |

**Total:** ~19-26 horas de trabalho, que podem ser distribuídas em 1-2 semanas.

---

## Nota final sobre o uso do plugin

O plugin Claude Code no IntelliJ **não lembra desta conversa** — cada sessão começa do zero. Por isso:

1. Mantenha este arquivo aberto em uma aba.
2. Na primeira mensagem da sessão, cole o **bloco de contexto fixo** do início deste documento.
3. Aí cole o **prompt específico do bloco** que você quer executar.
4. Ao final de cada bloco, revise o diff (`git diff`) antes de commitar.
5. Se o plugin sugerir desvios ("vou fazer X diferente pra ser mais prático") — leia bem antes de aceitar. A intenção das decisões arquiteturais deste plano é manter Clean Architecture; desvios fáceis comprometem isso.

Se em algum bloco o plugin travar em decisão (ex: "qual biblioteca usar pra Y?"), volte aqui e veja se está decidido. Se não estiver, me pergunta.
