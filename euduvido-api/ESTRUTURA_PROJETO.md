# Estrutura do Projeto - Eu Duvido API

## 📂 Árvore Completa de Pacotes

```
euduvido-api/
│
├── src/main/java/com/euduvido/euduvido_api/
│   │
│   ├── domain/                          ← CAMADA DE DOMÍNIO (Núcleo Puro)
│   │   ├── entities/
│   │   │   ├── User.java
│   │   │   ├── Challenge.java
│   │   │   ├── ChallengeParticipation.java
│   │   │   └── Proof.java
│   │   ├── enums/
│   │   │   ├── ChallengeStatus.java
│   │   │   ├── ParticipationStatus.java
│   │   │   └── MediaType.java
│   │   └── repositories/               ← Contratos (Interfaces)
│   │       ├── UserRepository.java
│   │       ├── ChallengeRepository.java
│   │       ├── ChallengeParticipationRepository.java
│   │       └── ProofRepository.java
│   │
│   ├── application/                    ← CAMADA DE CASOS DE USO
│   │   └── usecases/
│   │       ├── CreateUserUseCase.java
│   │       ├── CreateChallengeUseCase.java
│   │       ├── InviteUserToChallengeUseCase.java
│   │       ├── AcceptChallengeUseCase.java
│   │       ├── RefuseChallengeUseCase.java
│   │       ├── SubmitProofUseCase.java
│   │       ├── ApproveProofUseCase.java
│   │       ├── ListCreatedChallengesUseCase.java
│   │       ├── ListReceivedChallengesUseCase.java
│   │       └── UpdateExpiredChallengesUseCase.java
│   │
│   ├── infrastructure/                 ← CAMADA DE INFRAESTRUTURA
│   │   └── persistence/
│   │       ├── entities/               ← Entidades JPA
│   │       │   ├── UserEntity.java
│   │       │   ├── ChallengeEntity.java
│   │       │   ├── ChallengeParticipationEntity.java
│   │       │   └── ProofEntity.java
│   │       └── repositories/           ← Repositórios Spring Data JPA
│   │           ├── UserJpaRepository.java
│   │           ├── ChallengeJpaRepository.java
│   │           ├── ChallengeParticipationJpaRepository.java
│   │           └── ProofJpaRepository.java
│   │   └── repositories/               ← Implementações de Repositórios
│   │       ├── UserRepositoryImpl.java
│   │       ├── ChallengeRepositoryImpl.java
│   │       ├── ChallengeParticipationRepositoryImpl.java
│   │       └── ProofRepositoryImpl.java
│   │
│   ├── entrypoint/                     ← CAMADA DE ENTRADA (HTTP)
│   │   ├── controllers/
│   │   │   ├── UserController.java
│   │   │   ├── ChallengeController.java
│   │   │   ├── ParticipationController.java
│   │   │   └── ProofController.java
│   │   └── dtos/
│   │       ├── request/
│   │       │   ├── CreateUserRequest.java
│   │       │   ├── CreateChallengeRequest.java
│   │       │   └── SubmitProofRequest.java
│   │       └── response/
│   │           ├── UserResponse.java
│   │           ├── ChallengeResponse.java
│   │           ├── ChallengeParticipationResponse.java
│   │           └── ProofResponse.java
│   │
│   ├── config/                         ← CONFIGURAÇÕES
│   │   ├── UseCaseConfig.java          ← Injeção de Dependência dos Use Cases
│   │   └── CorsConfig.java             ← Configuração de CORS
│   │
│   ├── exception/                      ← TRATAMENTO DE EXCEÇÕES
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   │
│   └── EuDuvidoApiApplication.java     ← Classe Principal
│
├── src/main/resources/
│   └── application.properties          ← Configurações de Aplicação
│
├── pom.xml                             ← Configuração Maven
├── README_ARQUITETURA.md               ← Documentação Detalhada
└── ESTRUTURA_PROJETO.md                ← Este Arquivo
```

## 🎯 Fluxo de Requisição

### Exemplo: Criar Usuário

```
1. HTTP Request
   ↓
2. UserController.createUser()
   ↓
3. CreateUserRequest (Validação Bean)
   ↓
4. CreateUserUseCase.execute()
   - Valida email único
   - Chama factory method do Domain
   ↓
5. User.create() (Entidade de Domínio)
   - Validações de negócio
   - Factory method seguro
   ↓
6. UserRepository.save() (Interface do Domain)
   ↓
7. UserRepositoryImpl.save() (Implementação)
   - Converte para JPA
   ↓
8. UserJpaRepository.save() (Spring Data JPA)
   ↓
9. INSERT INTO users (SQL)
   ↓
10. UserEntity.toDomain() (Converter para Domain)
   ↓
11. UserResponse.fromDomain() (Converter para DTO)
   ↓
12. HTTP Response (JSON)
```

## 🔄 Dependências Entre Camadas

```
┌─────────────────────────────┐
│  ENTRYPOINT                 │
│  (Controllers, DTOs)        │
└──────────────┬──────────────┘
               │ depende de
┌──────────────▼──────────────┐
│  APPLICATION                │
│  (Use Cases)                │
└──────────────┬──────────────┘
               │ depende de
┌──────────────▼──────────────┐
│  DOMAIN                     │
│  (Entidades, Interfaces)    │
└──────────────┬──────────────┘
               │ depende de
┌──────────────▼──────────────┐
│  INFRASTRUCTURE             │
│  (JPA, Repositórios)        │
└─────────────────────────────┘
```

**IMPORTANTE**: O Domain NUNCA depende de outras camadas!

## 📋 Responsabilidades por Camada

### DOMAIN (Domínio)
- ✅ Validações de regra de negócio
- ✅ Estados das entidades
- ✅ Enumerações
- ✅ Interfaces de repositórios
- ❌ Sem anotações JPA
- ❌ Sem anotações Spring

### APPLICATION (Aplicação)
- ✅ Orquestração de casos de uso
- ✅ Coordenação entre entidades
- ✅ Lógica de aplicação
- ❌ Sem Controllers
- ❌ Sem acesso direto a HTTP

### INFRASTRUCTURE (Infraestrutura)
- ✅ Implementação de repositórios
- ✅ Entidades JPA
- ✅ Operações de persistência
- ✅ Queries SQL
- ❌ Sem regras de negócio

### ENTRYPOINT (Entrada)
- ✅ Controllers REST
- ✅ DTOs de request/response
- ✅ Validação de entrada
- ✅ Mapeamento DTO → Domain
- ❌ Sem regra de negócio
- ❌ Sem acesso direto ao banco

## 🔑 Padrões de Design Utilizados

### Factory Method
Usado em todas as entidades de domínio para garantir criação válida:

```java
// Domain Entity
public static User create(String name, String email, String password, String profileImageUrl) {
    validateUserData(name, email, password);
    return new User(null, name, email, password, profileImageUrl, LocalDateTime.now());
}
```

### Repository Pattern
Abstração de persistência via interfaces:

```java
// Domain Interface
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
}

// Infrastructure Implementation
@Component
public class UserRepositoryImpl implements UserRepository { ... }
```

### DTO Pattern
Separação entre dados de transferência e domínio:

```java
// Request DTO (entrada)
public class CreateUserRequest { ... }

// Response DTO (saída)
public class UserResponse { ... }

// Domain Entity (nunca é retornado direto)
public class User { ... }
```

### Adapter Pattern
Mapear entre entidades JPA e de domínio:

```java
// De Domain para JPA
UserEntity entity = UserEntity.fromDomain(user);

// De JPA para Domain
User user = entity.toDomain();
```

## 🧪 Como Testar

### Teste da Camada Domain
```java
@Test
public void shouldCreateUserWithValidData() {
    User user = User.create("João", "joao@email.com", "password123", null);
    assertNotNull(user.getId());
    assertEquals("João", user.getName());
}
```

### Teste de Use Case
```java
@Test
public void shouldCreateUserWithUseCase() {
    CreateUserUseCase useCase = new CreateUserUseCase(userRepository);
    User user = useCase.execute("João", "joao@email.com", "password123", null);
    verify(userRepository).save(any(User.class));
}
```

### Teste de Controller (Integração)
```java
@Test
public void shouldCreateUserViaAPI() {
    CreateUserRequest request = new CreateUserRequest("João", "joao@email.com", "password123", null);
    
    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("João"));
}
```

## 📊 Estatísticas do Projeto

| Métrica | Quantidade |
|---------|-----------|
| Entidades de Domínio | 4 |
| Enumerações | 3 |
| Interfaces de Repositório | 4 |
| Casos de Uso | 10 |
| Entidades JPA | 4 |
| Repositórios JPA | 4 |
| Implementações de Repositório | 4 |
| Controllers | 4 |
| DTOs Request | 3 |
| DTOs Response | 4 |
| Classes de Configuração | 2 |
| **Total de Classes** | **~50+** |

## 🚀 Próximas Evoluções

1. **Segurança**
   - [ ] JWT Authentication
   - [ ] OAuth2
   - [ ] BCrypt para senhas

2. **Persistência**
   - [ ] Migrar para PostgreSQL
   - [ ] Implementar migrations (Flyway/Liquibase)

3. **Features**
   - [ ] Paginação em listagens
   - [ ] Filtros e busca
   - [ ] Soft delete
   - [ ] Auditoria

4. **Performance**
   - [ ] Cache (Redis)
   - [ ] Async operations
   - [ ] Batch processing

5. **Testes**
   - [ ] Testes unitários completos
   - [ ] Testes de integração
   - [ ] Testes de carga

## 📚 Referências

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Spring Boot Best Practices](https://spring.io/guides)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)

---

**Estrutura criada para fins didáticos e demonstração de Clean Architecture**

