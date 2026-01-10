# 📌 REFERÊNCIA RÁPIDA - Eu Duvido API

## Executar Projeto
```bash
cd euduvido-api
mvn clean install
mvn spring-boot:run
# Acessar: http://localhost:8080
```

## Criar Usuário
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João",
    "email": "joao@email.com",
    "password": "senha123",
    "profileImageUrl": null
  }'
```

## Criar Desafio
```bash
curl -X POST "http://localhost:8080/api/v1/challenges?creatorId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Pule de paraquedas",
    "description": "Você consegue?",
    "deadline": "2024-02-15T23:59:59",
    "locationRequired": true
  }'
```

## Convidar Usuário
```bash
curl -X POST "http://localhost:8080/api/v1/challenges/1/invite?userId=2"
```

## Aceitar Desafio
```bash
curl -X POST "http://localhost:8080/api/v1/participations/1/accept"
```

## Enviar Comprovação
```bash
curl -X POST "http://localhost:8080/api/v1/participations/1/proof" \
  -H "Content-Type: application/json" \
  -d '{
    "mediaUrl": "https://example.com/prova.jpg",
    "mediaType": "PHOTO",
    "latitude": -23.5505,
    "longitude": -46.6333
  }'
```

## Aprovar Comprovação
```bash
curl -X POST "http://localhost:8080/api/v1/proofs/1/approve"
```

## Testar Projeto
```bash
mvn test
mvn test -Dtest=UserTest
mvn test -Dtest=ChallengeTest
```

## Limpar Build
```bash
mvn clean
```

## Ver Banco H2
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:euduvidobd
User: sa
Password: (vazio)
```

## Compilar
```bash
mvn compile
```

## Build JAR
```bash
mvn clean package -DskipTests
java -jar target/euduvido-api-0.0.1-SNAPSHOT.jar
```

## Documentação Rápida

**Entender**: README_PRIMEIRA_LEITURA.md
**Começar**: COMECE_AQUI.md
**Usar**: GUIA_USAR_API.md
**Desenvolver**: INSTRUCOES_DESENVOLVIMENTO.md
**Tudo**: INDICE.md

## Arquivos Importantes

- `pom.xml` - Dependências
- `application.properties` - Configuração
- `src/main/java/com/euduvido/euduvido_api/` - Código
- `src/test/java/com/euduvido/euduvido_api/` - Testes

## Estrutura Domain

1. Entidades: `domain/entities/`
2. Enums: `domain/enums/`
3. Repositórios: `domain/repositories/` (interfaces)

## Estrutura Application

Use Cases: `application/usecases/`

## Estrutura Infrastructure

1. Entities JPA: `infrastructure/persistence/entities/`
2. JPA Repos: `infrastructure/persistence/repositories/`
3. Implementações: `infrastructure/repositories/`

## Estrutura Entrypoint

1. Controllers: `entrypoint/controllers/`
2. DTOs: `entrypoint/dtos/`

## Adicionar Feature (10 Passos)

1. Criar entidade em `domain/entities/`
2. Criar interface em `domain/repositories/`
3. Criar use case em `application/usecases/`
4. Criar JPA entity em `infrastructure/persistence/entities/`
5. Criar JPA repo em `infrastructure/persistence/repositories/`
6. Criar implementação em `infrastructure/repositories/`
7. Criar DTOs em `entrypoint/dtos/`
8. Criar controller em `entrypoint/controllers/`
9. Registrar bean em `config/UseCaseConfig.java`
10. Escrever testes

## Validação

```bash
# Verificar compilação
mvn compile

# Verificar testes
mvn test

# Verificar tudo
mvn verify

# Checker de qualidade
mvn clean verify
```

## Porta 8080 Ocupada?

Mudar em `application.properties`:
```properties
server.port=8081
```

## IDE Recomendadas

- IntelliJ IDEA (melhor para Spring)
- VS Code + Extension Pack for Java
- Eclipse IDE

## Dúvidas?

1. COMECE_AQUI.md (Checklist)
2. INSTRUCOES_DESENVOLVIMENTO.md (Como fazer)
3. GUIA_USAR_API.md (Como usar)
4. README_ARQUITETURA.md (Entender)

## Próximas Melhorias

- [ ] Adicionar JWT
- [ ] Adicionar BCrypt
- [ ] Aumentar testes
- [ ] Migrar para PostgreSQL
- [ ] Adicionar cache Redis
- [ ] Configurar CI/CD
- [ ] Fazer deploy

## Status

✅ Estrutura: Pronta
✅ Código: Pronto
✅ Testes: Básicos
✅ Docs: Completa
⚠️ Segurança: Implementar
⚠️ Performance: Otimizar
❌ Deploy: Configurar

## Tempo

- Ler docs: 2 horas
- Executar: 30 minutos
- Entender código: 1 hora
- Estar produtivo: 4 horas

## Bom Desenvolvimento! 🚀

---

**Desenvolvido com ❤️ seguindo Clean Architecture**

