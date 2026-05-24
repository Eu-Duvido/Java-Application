# Eu Duvido — API Backend

Backend da plataforma de desafios acadêmicos **Eu Duvido**, construída com Spring Boot 4 e Java 21.

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|---------------|
| Java (JDK) | 21 |
| Docker + Docker Compose | 24 / v2 |
| Maven Wrapper | incluído (`mvnw`) |

> O Maven Wrapper (`./mvnw`) baixa o Maven automaticamente — não é necessário instalar o Maven globalmente.

---

## Configuração inicial (primeira vez)

### 1. Variáveis de ambiente

```bash
cp .env.example .env
```

Edite `.env` e preencha os valores:

```env
DB_URL=jdbc:mysql://localhost:3306/euduvido?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=euduvido
DB_PASSWORD=euduvidopass
JWT_SECRET=trocar-em-producao-64-chars-minimo-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
ANTHROPIC_API_KEY=          # opcional — validação IA desabilitada se vazio
STORAGE_PATH=./uploads
```

> **Nunca** commite o arquivo `.env` — ele já está no `.gitignore`.

### 2. Subir o banco de dados

```bash
docker compose up -d mysql
```

Aguarde até o health-check ficar `healthy`:

```bash
docker compose ps
```

### 3. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080/api/v1/`.

---

## Uso diário

```bash
# Iniciar banco (se não estiver rodando)
docker compose up -d mysql

# Rodar a aplicação
./mvnw spring-boot:run
```

---

## Testes

```bash
# Todos os testes (unit + controller)
./mvnw test

# Testes + relatório de cobertura JaCoCo
./mvnw verify
# Abrir: target/site/jacoco/index.html
```

> Os testes de repositório com Testcontainers exigem Docker Desktop rodando.
> Se Docker não estiver disponível, esses 4 testes são automaticamente pulados (skipped).

---

## Documentação da API

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

No Swagger UI, clique em **Authorize** e informe o token JWT obtido em `POST /api/v1/auth/login`:

```
Bearer <token>
```

---

## Autenticação rápida via curl

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu@email.com","password":"suasenha"}' | jq -r .token)

# Usar token em requests protegidos
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/challenges
```

---

## Configuração da API Anthropic (IA)

A validação de conteúdo usa a API do Claude (Anthropic). Para ativar:

1. Acesse https://console.anthropic.com e crie uma chave de API.
2. Adicione no `.env`:
   ```env
   ANTHROPIC_API_KEY=sk-ant-...
   ```
3. Reinicie a aplicação.

> Sem a chave, toda validação retorna `valid=true` (modo bypass).

---

## Estrutura do projeto

```
src/main/java/com/euduvido/euduvido_api/
├── domain/              # Entidades e repositórios puros (sem Spring/JPA)
├── application/
│   ├── usecases/        # Casos de uso (POJOs, sem @Service)
│   └── services/        # Interfaces de serviços externos
├── infrastructure/
│   ├── persistence/     # Entidades JPA + Spring Data repos
│   ├── repositories/    # Implementações dos repositórios do domínio
│   ├── security/        # JWT + BCrypt + filtros
│   ├── storage/         # Upload local de arquivos
│   ├── ai/              # Integração Anthropic
│   └── filter/          # RequestIdFilter (MDC)
├── entrypoint/
│   ├── controllers/     # REST controllers
│   └── dtos/            # Request/Response DTOs
├── config/              # SecurityConfig, CorsConfig, OpenApiConfig, UseCaseConfig
└── exception/           # GlobalExceptionHandler + ErrorResponse
```

---

## Formato padrão de erros

Todos os erros retornam JSON no formato:

```json
{
  "timestamp": "2025-04-22T10:30:00",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Validação falhou: 1 campo(s) inválido(s)",
  "errors": [
    { "field": "title", "message": "não deve estar em branco" }
  ]
}
```

| Código | Status | Situação |
|--------|--------|----------|
| `VALIDATION_ERROR` | 400 | Campos inválidos (Bean Validation) |
| `BUSINESS_ERROR` | 400 | Regra de negócio violada |
| `NOT_FOUND` | 404 | Recurso inexistente |
| `METHOD_NOT_ALLOWED` | 405 | Método HTTP incorreto |
| `CONFLICT` | 409 | Transição de estado inválida |
| `AI_VALIDATION_FAILED` | 422 | Conteúdo rejeitado pela IA |
| `FORBIDDEN` | 403 | Acesso negado |
| `INTERNAL_ERROR` | 500 | Erro inesperado |

---

## Reset do banco local

```bash
docker compose down -v   # remove volume com dados
docker compose up -d mysql
```

As migrations Flyway serão reexecutadas automaticamente na próxima inicialização da aplicação.

---

## Stack

- **Java 21** + **Spring Boot 4.0.1**
- **MySQL 8** (via Docker Compose)
- **Flyway** para migrations
- **Spring Security** + **JWT (JJWT 0.12)** + **BCrypt**
- **Springdoc OpenAPI 2.8** (Swagger UI)
- **Anthropic Claude** para validação de conteúdo
- **JUnit 5** + **Mockito** + **Testcontainers** para testes
- **JaCoCo** para cobertura
