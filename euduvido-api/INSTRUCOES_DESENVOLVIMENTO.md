# INSTRUÇÕES DE DESENVOLVIMENTO - Eu Duvido API

## 🎯 Como Trabalhar com Este Projeto

### Entendendo a Estrutura

Antes de iniciar o desenvolvimento, leia os documentos nesta ordem:

1. **SUMARIO_IMPLEMENTACAO.md** - Visão geral do que foi criado
2. **README_ARQUITETURA.md** - Explicação detalhada da arquitetura
3. **ESTRUTURA_PROJETO.md** - Estrutura de pacotes
4. **DIAGRAMA_ARQUITETURA.md** - Diagramas visuais
5. **GUIA_USAR_API.md** - Como usar os endpoints

---

## 📝 Padrão de Desenvolvimento

### Ao Adicionar uma Nova Feature

Siga este padrão para manter a qualidade arquitetural:

#### 1. Começar pelo DOMAIN (Entidade)

```java
// domain/entities/NovaEntidade.java
public class NovaEntidade {
    private Long id;
    private String campo1;
    
    private NovaEntidade(Long id, String campo1) {
        this.id = id;
        this.campo1 = campo1;
    }
    
    // Factory method para criação
    public static NovaEntidade create(String campo1) {
        validarDados(campo1); // Validações de domínio
        return new NovaEntidade(null, campo1);
    }
    
    // Factory method para recriar do banco
    public static NovaEntidade createFromDatabase(Long id, String campo1) {
        return new NovaEntidade(id, campo1);
    }
    
    private static void validarDados(String campo1) {
        if (campo1 == null || campo1.isEmpty()) {
            throw new IllegalArgumentException("Campo1 obrigatório");
        }
    }
    
    // Getters apenas
    public Long getId() { return id; }
    public String getCampo1() { return campo1; }
}
```

#### 2. Adicionar Interface de Repositório ao DOMAIN

```java
// domain/repositories/NovaEntidadeRepository.java
public interface NovaEntidadeRepository {
    NovaEntidade save(NovaEntidade entidade);
    Optional<NovaEntidade> findById(Long id);
    // ... outros métodos
}
```

#### 3. Criar Use Case na APPLICATION

```java
// application/usecases/CriarNovaEntidadeUseCase.java
public class CriarNovaEntidadeUseCase {
    private final NovaEntidadeRepository repository;
    
    public CriarNovaEntidadeUseCase(NovaEntidadeRepository repository) {
        this.repository = repository;
    }
    
    public NovaEntidade execute(String campo1) {
        NovaEntidade entidade = NovaEntidade.create(campo1);
        return repository.save(entidade);
    }
}
```

#### 4. Implementar Repositório na INFRASTRUCTURE

```java
// infrastructure/repositories/NovaEntidadeRepositoryImpl.java
@Component
public class NovaEntidadeRepositoryImpl implements NovaEntidadeRepository {
    private final NovaEntidadeJpaRepository jpaRepository;
    
    public NovaEntidadeRepositoryImpl(NovaEntidadeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public NovaEntidade save(NovaEntidade entidade) {
        NovaEntidadeEntity entity = NovaEntidadeEntity.fromDomain(entidade);
        NovaEntidadeEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<NovaEntidade> findById(Long id) {
        return jpaRepository.findById(id).map(NovaEntidadeEntity::toDomain);
    }
}
```

#### 5. Criar Entidade JPA na INFRASTRUCTURE

```java
// infrastructure/persistence/entities/NovaEntidadeEntity.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nova_entidade")
public class NovaEntidadeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String campo1;
    
    public NovaEntidade toDomain() {
        return NovaEntidade.createFromDatabase(id, campo1);
    }
    
    public static NovaEntidadeEntity fromDomain(NovaEntidade entidade) {
        NovaEntidadeEntity entity = new NovaEntidadeEntity();
        entity.setId(entidade.getId());
        entity.setCampo1(entidade.getCampo1());
        return entity;
    }
}
```

#### 6. Criar Repositório JPA na INFRASTRUCTURE

```java
// infrastructure/persistence/repositories/NovaEntidadeJpaRepository.java
@Repository
public interface NovaEntidadeJpaRepository extends JpaRepository<NovaEntidadeEntity, Long> {
    // Métodos customizados se necessário
}
```

#### 7. Criar DTOs no ENTRYPOINT

```java
// entrypoint/dtos/request/CriarNovaEntidadeRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarNovaEntidadeRequest {
    @NotBlank(message = "Campo1 é obrigatório")
    private String campo1;
}

// entrypoint/dtos/response/NovaEntidadeResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovaEntidadeResponse {
    private Long id;
    private String campo1;
    
    public static NovaEntidadeResponse fromDomain(NovaEntidade entidade) {
        return new NovaEntidadeResponse(
            entidade.getId(),
            entidade.getCampo1()
        );
    }
}
```

#### 8. Criar Controller no ENTRYPOINT

```java
// entrypoint/controllers/NovaEntidadeController.java
@RestController
@RequestMapping("/api/v1/nova-entidade")
public class NovaEntidadeController {
    private final CriarNovaEntidadeUseCase criarUseCase;
    
    public NovaEntidadeController(CriarNovaEntidadeUseCase criarUseCase) {
        this.criarUseCase = criarUseCase;
    }
    
    @PostMapping
    public ResponseEntity<NovaEntidadeResponse> criar(
            @Valid @RequestBody CriarNovaEntidadeRequest request) {
        var entidade = criarUseCase.execute(request.getCampo1());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(NovaEntidadeResponse.fromDomain(entidade));
    }
}
```

#### 9. Registrar Use Case em CONFIG

```java
// config/UseCaseConfig.java
@Bean
public CriarNovaEntidadeUseCase criarNovaEntidadeUseCase(
        NovaEntidadeRepository repository) {
    return new CriarNovaEntidadeUseCase(repository);
}
```

#### 10. Escrever Testes

```java
// test/domain/entities/NovaEntidadeTest.java
class NovaEntidadeTest {
    @Test
    void shouldCriarComDadosValidos() {
        var entidade = NovaEntidade.create("campo1");
        assertEquals("campo1", entidade.getCampo1());
    }
}
```

---

## 🔍 Checklist de Qualidade para PRs

Antes de fazer commit:

- [ ] Entidade de domínio criada com factory method
- [ ] Interface de repositório definida no domain
- [ ] Use case implementado
- [ ] Repositório JPA criado
- [ ] Implementação de repositório criada
- [ ] Entidade JPA mapeada corretamente
- [ ] DTOs request/response criados
- [ ] Controller implementado
- [ ] Use case registrado em UseCaseConfig
- [ ] Testes unitários escritos
- [ ] Sem lógica de negócio no Controller
- [ ] Sem exposição de entidades de domínio
- [ ] Validações em múltiplos níveis
- [ ] Exceptions tratadas apropriadamente
- [ ] Documentação atualizada

---

## 🚀 Fluxo de Trabalho Recomendado

### 1. Feature Branch
```bash
git checkout -b feature/nova-funcionalidade
```

### 2. Desenvolver Seguindo o Padrão
- Começar por DOMAIN
- Depois APPLICATION
- Depois INFRASTRUCTURE
- Depois ENTRYPOINT
- Depois TESTES

### 3. Testes
```bash
mvn test
mvn test -Dtest=NovaEntidadeTest
```

### 4. Build
```bash
mvn clean compile
mvn clean package -DskipTests
```

### 5. Verificar Qualidade
```bash
mvn clean verify
```

### 6. Commit e PR
```bash
git add .
git commit -m "feat: adicionar funcionalidade de nova entidade"
git push origin feature/nova-funcionalidade
```

---

## 🐛 Debugging

### Logs
Adicionar logs estratégicos (não em tudo!):

```java
// No Use Case
logger.info("Criando nova entidade: {}", campo1);

// Erros
logger.error("Erro ao salvar entidade", ex);
```

### Banco H2
Acessar console em: http://localhost:8080/h2-console

### Endpoints
Usar cURL ou Postman para testar:

```bash
curl -X POST http://localhost:8080/api/v1/nova-entidade \
  -H "Content-Type: application/json" \
  -d '{"campo1":"valor"}'
```

---

## 📚 Boas Práticas

### 1. Nomes Significativos
```java
// ✅ Bom
public class CreateChallengeUseCase { }

// ❌ Ruim
public class CreateUseCase { }
```

### 2. Métodos Pequenos
```java
// ✅ Bom
public void execute() {
    validate();
    create();
    save();
}

// ❌ Ruim
public void execute() {
    // 100 linhas de código misturado
}
```

### 3. Validações em Múltiplos Níveis
```java
// Nível 1: DTO (Entrypoint)
@NotBlank, @Email, @Size

// Nível 2: Domain (Entidade)
validateUserData()

// Nível 3: Repositório (Repository)
if (userRepository.existsByEmail(email))
```

### 4. Sem Magic Numbers
```java
// ✅ Bom
private static final int PASSWORD_MIN_LENGTH = 6;

// ❌ Ruim
if (password.length() < 6)
```

### 5. Documentação Inline
```java
/**
 * Cria um novo usuário
 * @param name Nome do usuário
 * @param email Email único
 * @param password Mínimo 6 caracteres
 * @return Usuário criado
 * @throws IllegalArgumentException se dados inválidos
 */
public User create(String name, String email, String password)
```

---

## 🔐 Segurança

### TODO: Implementar
- [ ] JWT Authentication
- [ ] Hash de senhas (BCrypt)
- [ ] Validação de email
- [ ] Rate limiting
- [ ] HTTPS

### Código Seguro
```java
// ❌ NÃO salvar senha em texto plano
user.setPassword("senha123");

// ✅ SEMPRE usar hash
user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
```

---

## 📊 Performance

### Evitar N+1 Queries
```java
// ❌ Ruim - N+1
for (Challenge challenge : challenges) {
    challenge.getCreator(); // Query por challenge
}

// ✅ Bom - Eager loading
@Query("SELECT c FROM Challenge c JOIN FETCH c.creator")
List<Challenge> findAll();
```

### Índices no Banco
```java
// No Entity
@Column(unique = true, nullable = false)
@Index(name = "idx_email")
private String email;
```

---

## 🧪 Testes

### Estrutura
```
test/
├── domain/
│   └── entities/
│       └── UserTest.java
├── application/
│   └── usecases/
│       └── CreateUserUseCaseTest.java
├── entrypoint/
│   └── controllers/
│       └── UserControllerTest.java
└── integration/
    └── UserIntegrationTest.java
```

### Exemplo de Teste
```java
@Test
void shouldCreateUserWithValidData() {
    // Arrange
    String name = "João";
    
    // Act
    User user = User.create(name, "joao@email.com", "senha123", null);
    
    // Assert
    assertEquals(name, user.getName());
}
```

---

## 📖 Referências

- [Clean Architecture - Robert Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Java Clean Code](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)

---

## 💬 FAQ

### P: Por que o Domain não pode depender de Spring?
**R:** Porque queremos que a lógica de negócio seja independente de tecnologia. Assim podemos testar sem Spring, reusar em outras plataformas, e trocar de framework sem reescrever tudo.

### P: Por que usar Factory Methods?
**R:** Para garantir que entidades sempre sejam criadas em estado válido, com todas as validações aplicadas.

### P: Como adicionar paginação?
**R:** Adicionar na interface do repositório:
```java
Page<Challenge> findAll(Pageable pageable);
```

### P: Posso adicionar queries customizadas?
**R:** Sim! Use @Query no JPA Repository:
```java
@Query("SELECT c FROM Challenge c WHERE c.status = ?1")
List<Challenge> findActive(ChallengeStatus status);
```

---

## 🎓 Próxima Etapa

Após dominar esta estrutura:

1. **Segurança**: Implementar JWT
2. **Testes**: 100% cobertura
3. **Performance**: Otimizar queries
4. **Cache**: Adicionar Redis
5. **Async**: Processamento assíncrono
6. **CI/CD**: Automação de deploy

---

**Happy Coding! 🚀**

