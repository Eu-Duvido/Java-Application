# Eu Duvido — Back-end (Java / Spring Boot)

API REST do projeto "Eu Duvido", responsável pela lógica de negócio, persistência e exposição dos endpoints usados pelo front-end/mobile.

Este repositório contém o serviço back-end implementado em Spring Boot que gerencia usuários, desafios, participações, comprovações e convites entre usuários.

Principais recursos
- Registro e gestão de usuários
- Criação e listagem de desafios
- Convites de usuários para participar de desafios (Invite) — CRUD completo e aceitar convite
- Submissão e aprovação de provas (mídia e localização)

Tecnologias
- Java 17+
- Spring Boot
- Maven
- JPA / Hibernate
- Banco relacional (ex.: MySQL)

Rápido guia de desenvolvimento (Windows)
1. Pré-requisitos
   - Java 17+
   - Maven (ou usar o wrapper `mvnw.cmd` incluso)
   - Banco de dados configurado (variáveis de ambiente ou `application.properties`)

2. Rodando localmente
   - Build: `mvnw.cmd clean package` (ou `mvn clean package`)
   - Executar: `mvnw.cmd spring-boot:run` ou `java -jar target/<artefato>.jar`

3. Variáveis importantes
   - `SPRING_DATASOURCE_URL` — URL do banco
   - `SPRING_DATASOURCE_USERNAME` — usuário do banco
   - `SPRING_DATASOURCE_PASSWORD` — senha do banco
   - `SPRING_PROFILES_ACTIVE` — perfil Spring (ex.: `dev`)

Banco de dados
O arquivo `src/main/resources/schema.sql` contém o esquema inicial — foi atualizado para incluir a tabela `invites` (convites entre usuários). Exemplo de tabela criada:
- `invites` (id, sender_id, recipient_id, message, accepted, created_at)

Endpoints principais (exemplos)
- POST /api/v1/users — criar usuário
- POST /api/v1/challenges — criar desafio
- POST /api/v1/challenges/{id}/invite?userId=... — convidar usuário para desafio

Novos endpoints para `Invite` (convites entre usuários)
- POST /api/v1/invites — criar convite (body: senderId, recipientId, message)
- GET /api/v1/invites/{id} — obter convite
- GET /api/v1/invites/sender/{senderId} — listar convites enviados
- GET /api/v1/invites/recipient/{recipientId} — listar convites recebidos
- POST /api/v1/invites/{id}/accept — aceitar um convite
- DELETE /api/v1/invites/{id} — deletar convite

Testes
- Executar testes: `mvnw.cmd test` ou `mvn test`

Contribuição
1. Abra uma issue descrevendo a mudança.
2. Crie um branch a partir de `main` e abra um pull request com descrição clara.

Status do que foi implementado nesta alteração
- [x] Entidade de domínio `Invite` (domínio)
- [x] Entidade JPA `InviteEntity` e `InviteJpaRepository`
- [x] Adapter `InviteRepositoryImpl`
- [x] DTOs de request/response para `Invite`
- [x] Casos de uso (Create/Get/List/Delete/Accept)
- [x] Controller REST `InviteController`
- [x] Atualização de `schema.sql` (tabela `invites`)

Próximos passos sugeridos
- Rodar `mvn -DskipTests package` localmente para validar compilação completa
- Criar testes unitários/integrados para os novos casos de uso e controller
- (Opcional) Adicionar eventos/notifications ao aceitar convite

---
Se quiser, eu posso também: gerar testes básicos JUnit para os use cases e controller, ou executar o build localmente aqui (se você permitir que eu rode o Maven).
