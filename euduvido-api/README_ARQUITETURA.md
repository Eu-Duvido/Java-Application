# Eu Duvido API - Backend

API REST do aplicativo mobile "Eu Duvido", desenvolvida com **Spring Boot** seguindo rigorosamente os princípios da **Clean Architecture**.

## 📋 Visão Geral

O "Eu Duvido" é uma aplicação que permite que usuários criem desafios e convidem outros usuários a participar, com sistema de comprovação por mídia (fotos/vídeos) e localização.

## 🏗️ Arquitetura Limpa (Clean Architecture)

O projeto está organizado em camadas bem definidas, onde **as dependências sempre apontam para dentro**:

```
┌─────────────────────────────────────────────┐
│         ENTRYPOINT (Controllers, DTOs)      │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│  APPLICATION (Casos de Uso - Use Cases)    │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│  DOMAIN (Entidades, Enums, Interfaces)     │
└─────────────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│ INFRASTRUCTURE (JPA, Repositórios)         │
└─────────────────────────────────────────────┘
```

### 1. **DOMAIN** (Núcleo do Sistema)
Contém as **regras de negócio puras**, sem dependência de frameworks.

**Pacotes:**
- `domain/entities/` - Entidades de domínio (User, Challenge, ChallengeParticipation, Proof)
- `domain/enums/` - Enumerações (ChallengeStatus, ParticipationStatus, MediaType)
- `domain/repositories/` - Interfaces de repositórios (contratos)

**Características:**
- ✅ Sem anotações JPA ou Spring
- ✅ Contém validações e regras de negócio
- ✅ Factory methods para criação segura de entidades
- ✅ Estados imutáveis via construtores privados

### 2. **APPLICATION** (Orquestração de Casos de Uso)
Implementa os **casos de uso** da aplicação, coordenando entidades e repositórios.

**Pacotes:**
- `application/usecases/` - Classes de caso de uso

**Casos de Uso Implementados:**
- `CreateUserUseCase` - Criar novo usuário
- `CreateChallengeUseCase` - Criar novo desafio
- `InviteUserToChallengeUseCase` - Convidar usuário para desafio
- `AcceptChallengeUseCase` - Aceitar convite
- `RefuseChallengeUseCase` - Recusar convite
- `SubmitProofUseCase` - Enviar comprovação
- `ApproveProofUseCase` - Aprovar comprovação
- `ListCreatedChallengesUseCase` - Listar desafios criados
- `ListReceivedChallengesUseCase` - Listar desafios recebidos
- `UpdateExpiredChallengesUseCase` - Atualizar desafios expirados

### 3. **INFRASTRUCTURE** (Implementações Técnicas)
Implementa os contratos do domain, integrando com banco de dados.

**Pacotes:**
- `infrastructure/persistence/entities/` - Entidades JPA
- `infrastructure/persistence/repositories/` - Repositórios Spring Data JPA
- `infrastructure/repositories/` - Implementações dos repositórios de domínio

**Responsabilidades:**
- Mapeamento entre entidades de domínio e JPA
- Operações de persistência
- Queries ao banco de dados

### 4. **ENTRYPOINT** (Interface com o Mundo Externo)
Camada HTTP da aplicação.

**Pacotes:**
- `entrypoint/controllers/` - Controllers REST
- `entrypoint/dtos/request/` - DTOs de entrada
- `entrypoint/dtos/response/` - DTOs de resposta

**Controllers:**
- `UserController` - Gerenciar usuários
- `ChallengeController` - Gerenciar desafios
- `ParticipationController` - Gerenciar participações
- `ProofController` - Gerenciar comprovações

### 5. **CONFIG & EXCEPTION**
Configurações gerais e tratamento de erros.

**Pacotes:**
- `config/` - Configurações de Bean, CORS, etc
- `exception/` - Tratamento global de exceções

## 🔄 Fluxo de Dependência

```
Controller → UseCase → Domain Entities & Interfaces
                            ↓
                    Repository Implementations
                            ↓
                        JPA Entities
                            ↓
                          Database
```

**Regra de Ouro:** O domain NUNCA depende de infrastructure ou entrypoint!

## 🗄️ Modelo de Dados

### User
```
- id: Long (PK)
- name: String
- email: String (UNIQUE)
- password: String
- profileImageUrl: String
- createdAt: LocalDateTime
```

### Challenge
```
- id: Long (PK)
- title: String
- description: String
- creator: User (FK)
- deadline: LocalDateTime
- status: ChallengeStatus (PENDING, ACTIVE, COMPLETED, EXPIRED)
- locationRequired: Boolean
- createdAt: LocalDateTime
```

### ChallengeParticipation
```
- id: Long (PK)
- user: User (FK)
- challenge: Challenge (FK)
- status: ParticipationStatus (INVITED, ACCEPTED, REFUSED, COMPLETED)
- createdAt: LocalDateTime
```

### Proof
```
- id: Long (PK)
- participation: ChallengeParticipation (FK)
- mediaUrl: String
- mediaType: MediaType (PHOTO, VIDEO)
- latitude: Double
- longitude: Double
- submittedAt: LocalDateTime
- approved: Boolean
```

## 📡 Endpoints

### Usuários
- `POST /api/v1/users` - Criar usuário

### Desafios
- `POST /api/v1/challenges` - Criar desafio
- `GET /api/v1/challenges/{id}` - Obter detalhes
- `POST /api/v1/challenges/{id}/invite` - Convidar usuário
- `GET /api/v1/challenges/creator/{creatorId}` - Listar desafios criados

### Participações
- `POST /api/v1/participations/{id}/accept` - Aceitar desafio
- `POST /api/v1/participations/{id}/refuse` - Recusar desafio
- `POST /api/v1/participations/{id}/proof` - Enviar comprovação
- `GET /api/v1/participations/user/{userId}` - Listar desafios recebidos

### Comprovações
- `POST /api/v1/proofs/{id}/approve` - Aprovar comprovação

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.8+

### Instalação e Execução

```bash
# Clone o repositório
cd euduvido-api

# Instale as dependências
mvn clean install

# Execute a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### Banco de Dados
- H2 Database (em memória)
- Console H2 em: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:euduvidobd`
- User: `sa`
- Password: (vazio)

## 🔍 Estrutura de Pacotes

```
com.euduvido.euduvido_api
├── domain/
│   ├── entities/
│   ├── enums/
│   └── repositories/
├── application/
│   └── usecases/
├── infrastructure/
│   ├── persistence/
│   │   ├── entities/
│   │   └── repositories/
│   └── repositories/
├── entrypoint/
│   ├── controllers/
│   └── dtos/
│       ├── request/
│       └── response/
├── config/
├── exception/
└── EuDuvidoApiApplication.java
```

## 💡 Princípios Aplicados

### ✅ Dependência Apontando para Dentro
- Controllers chamam Use Cases
- Use Cases usam Domain
- Infrastructure implementa contratos do Domain
- Domain é independente

### ✅ Separação de Responsabilidades
- Domain: Regras de negócio
- Application: Orquestração
- Infrastructure: Persistência
- Entrypoint: HTTP

### ✅ Factory Methods
Todas as entidades usam factory methods para garantir validações:

```java
// ❌ Errado (construtor público)
User user = new User(null, "", "", "");

// ✅ Certo (factory method com validações)
User user = User.create(name, email, password, profileImageUrl);
```

### ✅ DTOs para Comunicação
- Nunca retorna entidades de domínio diretamente
- Controllers convertem entidades em DTOs
- Validação de entrada via Bean Validation

### ✅ Tratamento de Exceções
- Exceções de domínio (IllegalArgumentException, IllegalStateException)
- Tratamento global via GlobalExceptionHandler
- Respostas consistentes em JSON

## 📦 Dependências Principais

```xml
<!-- Spring Boot -->
<dependency>spring-boot-starter-web</dependency>
<dependency>spring-boot-starter-data-jpa</dependency>
<dependency>spring-boot-starter-validation</dependency>

<!-- Database -->
<dependency>h2database/h2</dependency>

<!-- Lombok -->
<dependency>org.projectlombok/lombok</dependency>
```

## 🎓 Aprendizados e Boas Práticas

1. **Domain Puro**: Sem frameworks, testável isoladamente
2. **Casos de Uso Simples**: Cada classe faz uma coisa bem
3. **Inversão de Dependência**: Usa interfaces (repositórios)
4. **Factory Methods**: Garante criação válida de objetos
5. **Imutabilidade**: Getters apenas, sem setters públicos
6. **Separação de Camadas**: Cada camada tem responsabilidade clara

## 📝 Exemplo de Fluxo Completo

### Criando um Usuário

1. **Entrypoint (Controller)**
   ```java
   @PostMapping
   public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
       var user = createUserUseCase.execute(...);
       return ResponseEntity.status(HttpStatus.CREATED)
           .body(UserResponse.fromDomain(user));
   }
   ```

2. **Application (Use Case)**
   ```java
   public User execute(String name, String email, String password, String profileImageUrl) {
       if (userRepository.existsByEmail(email)) {
           throw new IllegalArgumentException("Email já cadastrado");
       }
       User newUser = User.create(name, email, password, profileImageUrl);
       return userRepository.save(newUser);
   }
   ```

3. **Domain (Entidade)**
   ```java
   public static User create(String name, String email, String password, String profileImageUrl) {
       validateUserData(name, email, password);
       return new User(null, name, email, password, profileImageUrl, LocalDateTime.now());
   }
   ```

4. **Infrastructure (Repositório)**
   ```java
   public User save(User user) {
       UserEntity entity = UserEntity.fromDomain(user);
       UserEntity saved = jpaRepository.save(entity);
       return saved.toDomain();
   }
   ```

## 🔐 Segurança Futura

Próximos passos para produção:
- [ ] Implementar JWT/OAuth2
- [ ] Hash de passwords (BCrypt)
- [ ] Validação de email
- [ ] Rate limiting
- [ ] HTTPS
- [ ] Database relacional (PostgreSQL)

---

**Desenvolvido com ❤️ seguindo Clean Architecture**

