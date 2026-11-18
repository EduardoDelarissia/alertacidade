# 🚨 AlertaCidade — Sistema Inteligente de Monitoramento e Alertas

![Java](https://img.shields.io/badge/Java-Spring%20Boot-orange)
![Oracle](https://img.shields.io/badge/Database-Oracle-red)
![API](https://img.shields.io/badge/API-REST-blue)
![Status](https://img.shields.io/badge/Status-Integração%20Validada-brightgreen)

## 📌 Sobre o projeto

O **AlertaCidade** é um projeto acadêmico desenvolvido para a Global Solution 2026 da FIAP, com o objetivo de contribuir para a prevenção e o monitoramento de situações de risco em áreas urbanas.

A proposta consiste em utilizar dados de pontos de monitoramento e sensores para identificar situações de atenção ou risco crítico, permitindo o registro e o acompanhamento de alertas.

O sistema busca apoiar iniciativas de cidades inteligentes, prevenção de enchentes e segurança da população.

## 🎯 Objetivos

- Monitorar situações de risco relacionadas ao nível da água.
- Registrar informações de sensores e pontos de monitoramento.
- Identificar situações de atenção e risco crítico.
- Disponibilizar alertas por meio de uma API REST.
- Armazenar informações em um banco de dados Oracle.
- Permitir o acompanhamento de alertas abertos e encerrados.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Utilização |
|---|---|
| Java | Desenvolvimento do backend |
| Spring Boot | Construção da API REST |
| Oracle Database | Armazenamento dos dados |
| Oracle SQL Developer | Consultas e validação do banco |
| Maven | Gerenciamento de dependências |
| Postman | Testes dos endpoints |
| IntelliJ IDEA | Desenvolvimento da aplicação |
| Git e GitHub | Versionamento do projeto |

## 🏗️ Arquitetura da aplicação

O AlertaCidade utiliza uma arquitetura baseada em API REST, permitindo a comunicação entre clientes HTTP, aplicação Java e banco de dados.

```text
         CLIENTE HTTP
       (Postman / Web)
              |
              v
       API REST - JAVA
         Spring Boot
              |
              v
       Camada de Acesso
           a Dados
              |
              v
       ORACLE DATABASE
              |
       +------+------+
       |             |
  Monitoramento   Alertas
   e Sensores     de Risco
```

## 🗄️ Banco de dados Oracle

O banco de dados utilizado no projeto é o Oracle disponibilizado pela FIAP.

As principais tabelas são:

| Tabela | Descrição |
|---|---|
| T_AC_PONTO_MONITORAMENTO | Pontos de monitoramento |
| T_AC_SENSOR | Sensores cadastrados |
| T_AC_MEDICAO | Medições realizadas |
| T_AC_ALERTA | Alertas registrados |

### Dados validados

Durante os testes, foram identificados os seguintes registros iniciais:

| Entidade | Registros |
|---|---:|
| Pontos de monitoramento | 6 |
| Sensores | 6 |
| Medições | 15 |
| Alertas | 5 |

Posteriormente, um novo alerta foi criado pela API e confirmado diretamente no Oracle.

## ⚙️ Configuração do backend

O projeto utiliza perfis de configuração do Spring Boot.

O perfil `dev` é destinado ao ambiente de desenvolvimento, enquanto o perfil `prod` permite utilizar o Oracle FIAP.

### Configuração Oracle

Arquivo:

`src/main/resources/application-prod.properties`

```properties
spring.datasource.url=${DB_URL:jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.sql.init.mode=never
spring.jpa.hibernate.ddl-auto=none

spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=20000
```

As credenciais são configuradas por variáveis de ambiente, evitando a exposição de informações sensíveis no repositório.

Variáveis necessárias:

```text
SPRING_PROFILES_ACTIVE=prod
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

A URL do banco pode ser sobrescrita pela variável `DB_URL`.

## 🚀 Executando a aplicação

1. Clone o repositório do GitHub.
2. Abra o projeto no IntelliJ IDEA.
3. Localize o módulo `backend-java`.
4. Configure as variáveis de ambiente.
5. Verifique as dependências do Maven.
6. Execute a classe `AlertaCidadeApplication.java`.

Quando a inicialização é concluída, o Spring Boot apresenta mensagens semelhantes a:

```text
Tomcat started on port 8080 (http)
Started AlertaCidadeApplication
```

A aplicação fica disponível localmente em:

`http://localhost:8080`

## 🌐 API REST — Alertas

### Consultar alertas

**Método:** `GET`

```http
GET http://localhost:8080/api/alertas
```

Esse endpoint retorna os alertas cadastrados no sistema em formato JSON.

### Criar um alerta

**Método:** `POST`

```http
POST http://localhost:8080/api/alertas
Content-Type: application/json
```

Exemplo de requisição:

```json
{
  "pontoId": 13,
  "nivel": "ATENCAO",
  "mensagem": "Teste de integracao Java com Oracle FIAP",
  "status": "ABERTO"
}
```

### Resultado obtido

Durante os testes realizados pelo Postman, a API retornou:

**HTTP 201 Created**

O novo alerta recebeu o identificador:

```json
{
  "id": 16,
  "pontoId": 13,
  "medicaoId": null,
  "nivel": "ATENCAO",
  "mensagem": "Teste de integracao Java com Oracle FIAP",
  "status": "ABERTO",
  "dataEncerramento": null,
  "aberto": true
}
```

O retorno confirmou que a API recebeu e processou a solicitação de criação do alerta.

## ✅ Comprovação da integração Java + Oracle

Para verificar se o registro foi realmente armazenado no banco Oracle, foi executada a seguinte consulta no SQL Developer:

```sql
SELECT *
FROM T_AC_ALERTA
WHERE ID_ALERTA = 16;
```

### Resultado

A consulta retornou o alerta com:

- **ID:** 16
- **Ponto de monitoramento:** 13
- **Nível:** ATENCAO
- **Mensagem:** Teste de integracao Java com Oracle FIAP
- **Status:** ABERTO

Isso comprova que o registro criado pela API Java foi persistido e pôde ser consultado diretamente no Oracle FIAP.

### Fluxo validado

```text
POSTMAN
   |
   | POST /api/alertas
   v
SPRING BOOT
   |
   | Processamento da requisição
   v
ORACLE FIAP
   |
   | Registro armazenado
   v
SQL DEVELOPER
   |
   | SELECT WHERE ID_ALERTA = 16
   v
REGISTRO CONFIRMADO
```

## 📸 Evidências dos testes

Foram realizadas verificações utilizando IntelliJ IDEA, Postman, navegador e Oracle SQL Developer.

As evidências incluem:

1. Inicialização bem-sucedida da aplicação Spring Boot.
2. Consulta dos registros existentes no Oracle.
3. Listagem de alertas pela API REST.
4. Criação de um alerta utilizando o Postman.
5. Retorno HTTP `201 Created`.
6. Consulta SQL confirmando o alerta ID 16.

Essas evidências demonstram o funcionamento da integração entre a aplicação Java e o banco de dados Oracle.

## 🔐 Segurança

As credenciais de acesso ao banco não devem ser armazenadas diretamente no código-fonte.

O projeto utiliza variáveis de ambiente para configurar usuário, senha e endereço de conexão.

Arquivos locais com senhas, configurações privadas e outros dados sensíveis não devem ser enviados ao GitHub.

## 📊 Resultados alcançados

- Aplicação Spring Boot inicializada com sucesso.
- Banco de dados Oracle acessível pelo SQL Developer.
- Estrutura de tabelas consultada e validada.
- Endpoint GET retornando alertas cadastrados.
- Endpoint POST criando um novo alerta.
- Resposta HTTP `201 Created` recebida.
- Persistência do alerta ID 16 confirmada no Oracle.

## 🔮 Próximas melhorias

Entre as possibilidades de evolução do projeto estão:

- Desenvolvimento de um dashboard web.
- Visualização dos pontos de monitoramento em um mapa.
- Integração com sensores IoT.
- Atualização automática dos dados.
- Notificações para alertas críticos.
- Gráficos e relatórios históricos.
- Melhorias de autenticação e segurança.

## 🎓 Informações acadêmicas

**Instituição:** FIAP — Faculdade de Informática e Administração Paulista

**Projeto:** AlertaCidade

**Contexto:** Global Solution 2026 — CitizenTech

**Desenvolvedor:** Eduardo Antonio Delarissia

**RM:** 563468

**Ano:** 2026

---

## 🏁 Conclusão

O AlertaCidade demonstrou o funcionamento de sua API REST integrada ao banco de dados Oracle FIAP.

A criação de um alerta pelo Postman e sua posterior identificação no SQL Developer confirmaram a persistência dos dados.

O resultado representa uma etapa importante no desenvolvimento de uma solução tecnológica voltada ao monitoramento urbano e à prevenção de situações de risco.

**AlertaCidade — Tecnologia para cidades mais seguras.**
