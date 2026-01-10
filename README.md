# Java-Application

Back-end do projeto `Eu Duvido`\. Serviço REST desenvolvido com Spring Boot para a lógica de negócio do projeto\.

## Descrição
Aplicação back-end responsável por expor endpoints REST para o projeto `Eu Duvido`\. Implementa autenticação, persistência e regras de negócio principais\.

## Tecnologias
- Java 17\+
- Spring Boot
- Maven
- Banco de dados relacional (ex.: MySQL)

## Pré\-requisitos
- Java 17 ou superior instalado
- Maven instalado ou usar o wrapper `mvnw.cmd`
- Variáveis de ambiente configuradas para conexão com o banco

## Executando localmente \(Windows\)
1\. Ajustar variáveis de ambiente necessárias (ex.: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`)\.
2\. Build:
- Usando wrapper: `mvnw.cmd clean package`
- Ou Maven: `mvn clean package`
  3\. Executar:
- `mvnw.cmd spring-boot:run`
- Ou executar o jar gerado: `java -jar target\<seu-artefato\>.jar`

A aplicação por padrão inicia na porta `8080` a menos que configurado de outra forma via `SPRING_PROFILES_ACTIVE` ou `application.properties`/`application.yml`\.

## Variáveis de ambiente comuns
- `SPRING_DATASOURCE_URL` \- URL do banco de dados
- `SPRING_DATASOURCE_USERNAME` \- usuário do banco
- `SPRING_DATASOURCE_PASSWORD` \- senha do banco
- `SPRING_PROFILES_ACTIVE` \- perfil Spring ativo (ex.: `dev`, `prod`)

## Endpoints (exemplo)
- `GET /actuator/health` \- status da aplicação (se actuator estiver habilitado)
- Endpoints da API ficam sob `/api/*` \(exemplo: `GET /api/users`\)\.
  Consultar os controllers do projeto para lista completa de rotas\.

## Testes
Executar testes unitários e de integração:
- `mvnw.cmd test` ou `mvn test`

## Estrutura do projeto
- `src/main/java` \- código fonte
- `src/main/resources` \- configurações e arquivos estáticos
- `pom.xml` \- configuração do Maven

## Contribuição
- Abrir issues para bugs ou melhorias
- Criar branches a partir de `main` e abrir pull requests com descrição clara das mudanças\.

