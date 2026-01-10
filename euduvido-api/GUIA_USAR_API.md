# Guia de Uso - Eu Duvido API

## 🚀 Iniciando a Aplicação

### Pré-requisitos
- Java 21+
- Maven 3.8+
- Git

### Passos para Executar

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd euduvido-api

# 2. Instale as dependências
mvn clean install

# 3. Execute a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🗄️ Banco de Dados

### H2 Database
- **Tipo**: H2 em memória (reseta ao reiniciar a aplicação)
- **JDBC URL**: `jdbc:h2:mem:euduvidobd`
- **Usuário**: `sa`
- **Senha**: (vazia)
- **Console**: `http://localhost:8080/h2-console`

## 📡 Endpoints da API

### 1. USUÁRIOS

#### Criar Usuário
```http
POST /api/v1/users
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123",
  "profileImageUrl": "https://example.com/foto.jpg"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "profileImageUrl": "https://example.com/foto.jpg",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### 2. DESAFIOS

#### Criar Desafio
```http
POST /api/v1/challenges?creatorId=1
Content-Type: application/json

{
  "title": "Pule de paraquedas",
  "description": "Você consegue pular de um avião com paraquedas?",
  "deadline": "2024-02-15T23:59:59",
  "locationRequired": true
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Pule de paraquedas",
  "description": "Você consegue pular de um avião com paraquedas?",
  "creator": {
    "id": 1,
    "name": "João Silva",
    "email": "joao@email.com",
    "profileImageUrl": "https://example.com/foto.jpg",
    "createdAt": "2024-01-15T10:30:00"
  },
  "deadline": "2024-02-15T23:59:59",
  "status": "PENDING",
  "locationRequired": true,
  "createdAt": "2024-01-15T10:35:00"
}
```

#### Listar Desafios Criados
```http
GET /api/v1/challenges/creator/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Pule de paraquedas",
    "description": "Você consegue pular de um avião com paraquedas?",
    "creator": { ... },
    "deadline": "2024-02-15T23:59:59",
    "status": "PENDING",
    "locationRequired": true,
    "createdAt": "2024-01-15T10:35:00"
  }
]
```

#### Convidar Usuário para Desafio
```http
POST /api/v1/challenges/1/invite?userId=2
```

**Response (200 OK):**
```json
{
  "message": "Usuário convidado com sucesso"
}
```

---

### 3. PARTICIPAÇÕES

#### Listar Desafios Recebidos
```http
GET /api/v1/participations/user/2
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "user": {
      "id": 2,
      "name": "Maria Silva",
      "email": "maria@email.com",
      "profileImageUrl": "https://example.com/maria.jpg",
      "createdAt": "2024-01-15T10:40:00"
    },
    "challenge": { ... },
    "status": "INVITED",
    "createdAt": "2024-01-15T10:45:00"
  }
]
```

#### Aceitar Desafio
```http
POST /api/v1/participations/1/accept
```

**Response (200 OK):**
```json
{
  "id": 1,
  "user": { ... },
  "challenge": { ... },
  "status": "ACCEPTED",
  "createdAt": "2024-01-15T10:45:00"
}
```

#### Recusar Desafio
```http
POST /api/v1/participations/1/refuse
```

**Response (200 OK):**
```json
{
  "id": 1,
  "user": { ... },
  "challenge": { ... },
  "status": "REFUSED",
  "createdAt": "2024-01-15T10:45:00"
}
```

#### Enviar Comprovação
```http
POST /api/v1/participations/1/proof
Content-Type: application/json

{
  "mediaUrl": "https://example.com/prova.jpg",
  "mediaType": "PHOTO",
  "latitude": -23.5505,
  "longitude": -46.6333
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "participationId": 1,
  "mediaUrl": "https://example.com/prova.jpg",
  "mediaType": "PHOTO",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "submittedAt": "2024-01-15T11:00:00",
  "approved": false
}
```

---

### 4. COMPROVAÇÕES

#### Aprovar Comprovação
```http
POST /api/v1/proofs/1/approve
```

**Response (200 OK):**
```json
{
  "id": 1,
  "participationId": 1,
  "mediaUrl": "https://example.com/prova.jpg",
  "mediaType": "PHOTO",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "submittedAt": "2024-01-15T11:00:00",
  "approved": true
}
```

---

## 🔄 Fluxo Completo de Uso

### Cenário: João cria um desafio e convida Maria

#### Passo 1: Criar usuários

**João:**
```http
POST /api/v1/users
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123",
  "profileImageUrl": "https://example.com/joao.jpg"
}
```
Retorna: `id: 1`

**Maria:**
```http
POST /api/v1/users
{
  "name": "Maria Santos",
  "email": "maria@email.com",
  "password": "senha456",
  "profileImageUrl": "https://example.com/maria.jpg"
}
```
Retorna: `id: 2`

#### Passo 2: João cria um desafio

```http
POST /api/v1/challenges?creatorId=1
{
  "title": "Nado em água fria",
  "description": "Nade em água do mar no inverno",
  "deadline": "2024-02-15T23:59:59",
  "locationRequired": true
}
```
Retorna: `id: 1`

#### Passo 3: João convida Maria

```http
POST /api/v1/challenges/1/invite?userId=2
```

#### Passo 4: Maria vê desafios recebidos

```http
GET /api/v1/participations/user/2
```
Retorna lista com participação `id: 1, status: INVITED`

#### Passo 5: Maria aceita o desafio

```http
POST /api/v1/participations/1/accept
```
Status muda para: `ACCEPTED`

#### Passo 6: Maria envia comprovação

```http
POST /api/v1/participations/1/proof
{
  "mediaUrl": "https://s3.amazonaws.com/videos/prova.mp4",
  "mediaType": "VIDEO",
  "latitude": -23.5505,
  "longitude": -46.6333
}
```
Retorna: `id: 1, approved: false`

#### Passo 7: João aprova a comprovação

```http
POST /api/v1/proofs/1/approve
```
Status muda para: `approved: true`

---

## ❌ Tratamento de Erros

### Validação de Entrada (400 Bad Request)
```json
{
  "status": 400,
  "message": "Validação falhou",
  "errors": {
    "name": "Nome é obrigatório",
    "email": "Email deve ser válido"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Regra de Negócio Violada (400 Bad Request)
```json
{
  "status": 400,
  "message": "Email já cadastrado",
  "errors": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Conflito de Estado (409 Conflict)
```json
{
  "status": 409,
  "message": "Apenas convites podem ser aceitos",
  "errors": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Recurso Não Encontrado (404 Not Found)
```json
{
  "status": 404,
  "message": "Usuário não encontrado",
  "errors": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 🧪 Testes com cURL

### Criar Usuário
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123",
    "profileImageUrl": "https://example.com/foto.jpg"
  }'
```

### Criar Desafio
```bash
curl -X POST http://localhost:8080/api/v1/challenges?creatorId=1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Pule de paraquedas",
    "description": "Pule de um avião",
    "deadline": "2024-02-15T23:59:59",
    "locationRequired": true
  }'
```

### Convidar Usuário
```bash
curl -X POST http://localhost:8080/api/v1/challenges/1/invite?userId=2
```

### Aceitar Desafio
```bash
curl -X POST http://localhost:8080/api/v1/participations/1/accept
```

### Listar Desafios Recebidos
```bash
curl http://localhost:8080/api/v1/participations/user/2
```

---

## 🔐 Validações

### Email
- Deve conter "@"
- Deve ser único no sistema

### Senha
- Mínimo 6 caracteres
- Máximo 50 caracteres

### Nome
- Mínimo 3 caracteres
- Máximo 100 caracteres

### Deadline
- Deve ser no futuro
- Formato: ISO 8601 (2024-02-15T23:59:59)

### MediaType
- Valores válidos: `PHOTO`, `VIDEO`

---

## 📝 Notas Importantes

1. **Banco de Dados em Memória**: Todos os dados são perdidos ao reiniciar
2. **CORS Habilitado**: API aceita requisições de qualquer origem
3. **Validação Automática**: Bean Validation valida DTOs de request
4. **Sem Autenticação**: Versão atual não possui autenticação (adicionar futura)
5. **Timestamps em UTC**: Todos os timestamps usam ISO 8601

---

## 🔗 Recursos Úteis

- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [JPA/Hibernate](https://hibernate.org/orm/)
- [RESTful API Best Practices](https://restfulapi.net/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Para mais informações, consulte README_ARQUITETURA.md e ESTRUTURA_PROJETO.md**

