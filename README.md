# 🚨 AlertaCidade — Sistema Inteligente de Monitoramento e Alertas

![Java](https://img.shields.io/badge/Java-Spring%20Boot-orange)
![Oracle](https://img.shields.io/badge/Database-Oracle-red)
![API](https://img.shields.io/badge/API-REST-blue)
![Status](https://img.shields.io/badge/Integra%C3%A7%C3%A3o-Validada-brightgreen)
![Deploy](https://img.shields.io/badge/Deploy-Render-46E3B7)

## 📋 Ficha da CitizenTech — base comum da equipe

> **Ficha de alinhamento do projeto (Global Solution 2026.2).** A cidade e o bairro abaixo são uma **área de referência proposta**, a confirmar pela equipe. O indicador de impacto é uma **métrica proposta**, ainda sem resultado mensurado.

| Campo | Descrição |
|---|---|
| **Nome da CitizenTech** | AlertaCidade |
| **Cidade e bairro** | São Paulo (SP) — Bela Vista ; foco em áreas urbanas sujeitas a enchentes |
| **Cidadão beneficiado** | Moradores de áreas vulneráveis a alagamentos e enchentes, especialmente quem precisa de informações sobre riscos locais |
| **Problema, em uma frase** | A falta de informações organizadas e alertas acessíveis dificulta o acompanhamento de riscos de enchentes em áreas urbanas. |
| **Como funciona** | Uma API REST recebe, registra e consulta dados de pontos de monitoramento, sensores, medições e alertas, persistidos no Oracle FIAP e disponibilizados pelo backend hospedado no Render. |
| **O que cada parte faz** | **Java/Spring Boot:** processa requisições e regras da API; **Oracle FIAP:** armazena registros; **Postman/Swagger:** apoiam testes e documentação; **Docker/Render:** empacotam e hospedam o backend. Dashboard, mapa e sensores IoT são melhorias futuras, não funcionalidades já comprovadas. |
| **Link do repositório** | https://github.com/EduardoDelarissia/alertacidade |
| **Indicador de impacto** | **Proposto:** percentual de alertas críticos registrados e disponibilizados pela API, medido por período. Não há indicador de impacto social aferido até o momento. |
| **ODS conectados** | **ODS 11:** Cidades e Comunidades Sustentáveis; **ODS 13:** Ação Contra a Mudança Global do Clima. |

## 📌 Sobre o projeto

O **AlertaCidade** é um projeto acadêmico desenvolvido para a Global Solution 2026 da FIAP, dentro da temática CitizenTech.

O objetivo é contribuir para o monitoramento de situações de risco em áreas urbanas, especialmente aquelas relacionadas a enchentes e ao aumento do nível da água.

A aplicação permite organizar informações sobre pontos de monitoramento, sensores, medições e alertas, utilizando uma API REST desenvolvida em Java com Spring Boot e integrada ao banco de dados Oracle FIAP.

## 🌐 Deploy em produção — Render

O backend foi publicado no **Render** em 19/11/2026. O painel exibiu **Deploy succeeded | Live**, e a consulta pública de alertas retornou registros JSON.

- **URL do serviço:** https://alertacidade.onrender.com
- **Consultar alertas (GET):** https://alertacidade.onrender.com/api/alertas
- **Swagger / OpenAPI:** https://alertacidade.onrender.com/swagger-ui.html *(acesso sujeito à configuração da aplicação)*
- **Repositório GitHub:** https://github.com/EduardoDelarissia/alertacidade

> **Observação:** a rota raiz `/` pode retornar HTTP 404 porque a aplicação fornece endpoints REST e não necessariamente uma página inicial. Para testar o deploy, utilize `/api/alertas`. Em instâncias gratuitas do Render, a primeira requisição após inatividade pode demorar mais.

### Evidências do deploy

Os registros do Render mostraram:

```text
Deploy succeeded | Live
Your service is live
Available at your primary URL https://alertacidade.onrender.com
Tomcat started on port 8080 (http)
Started AlertaCidadeApplication
HikariPool-1 - Added connection oracle.jdbc.driver.T4CConnection
HikariPool-1 - Start completed.
```

A inicialização do pool de conexões Oracle e a resposta JSON da API em produção demonstram o funcionamento do backend publicado. A comprovação detalhada está no PDF de evidências do projeto.

## 🎯 Objetivos

- Registrar pontos de monitoramento urbano.
- Organizar informações de sensores.
- Armazenar medições relacionadas ao nível da água.
- Identificar situações de atenção e risco crítico.
- Registrar e consultar alertas.
- Permitir o acompanhamento de alertas abertos e encerrados.
- Disponibilizar dados por meio de uma API REST.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Utilização |
|---|---|
| Java | Desenvolvimento do backend |
| Spring Boot | API REST |
| Spring JDBC | Acesso ao banco de dados |
| Oracle Database | Persistência dos dados |
| Oracle SQL Developer | Consultas e validações SQL |
| Maven | Gerenciamento de dependências |
| Postman | Testes das requisições HTTP |
| Swagger / OpenAPI | Documentação interativa da API |
| IntelliJ IDEA | Ambiente de desenvolvimento |
| Git e GitHub | Versionamento |
| Docker | Empacotamento para deploy |
| Render | Hospedagem do backend |

## 🏗️ Arquitetura

O backend utiliza Spring Boot para receber e processar requisições HTTP, comunicando-se com o banco Oracle por meio de JDBC.

```text
       CLIENTE HTTP
    (Postman / Navegador)
             |
             v
       API REST JAVA
        Spring Boot
             |
             v
        Spring JDBC
             |
             v
       ORACLE FIAP
             |
    +--------+---------+
    |        |         |
  Pontos   Sensores  Alertas
    |
  Medições
```

Em produção, o cliente acessa a API hospedada no Render, que utiliza as variáveis de ambiente configuradas para conectar-se ao Oracle FIAP.

## 🗄️ Banco de dados

O projeto utiliza o Oracle Database disponibilizado pela FIAP.

### Principais tabelas

| Tabela | Descrição |
|---|---|
| T_AC_PONTO_MONITORAMENTO | Pontos de monitoramento |
| T_AC_SENSOR | Sensores cadastrados |
| T_AC_MEDICAO | Medições registradas |
| T_AC_ALERTA | Alertas de risco |

### Dados iniciais validados

| Entidade | Quantidade |
|---|---:|
| Pontos de monitoramento | 6 |
| Sensores | 6 |
| Medições | 15 |
| Alertas | 5 |

Posteriormente, foi criado um novo alerta pela API REST, recebendo o identificador **16**. Esse registro foi confirmado diretamente no Oracle SQL Developer.

## ⚙️ Configuração do backend

O projeto utiliza diferentes perfis de configuração do Spring Boot:

- `dev`: ambiente de desenvolvimento, utilizando H2.
- `prod`: ambiente de produção, configurado para utilizar Oracle FIAP.

### Configuração Oracle

Arquivo: `backend-java/src/main/resources/application-prod.properties` (dentro do diretório do backend no repositório).

```properties
spring.datasource.url=${DB_URL:jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.sql.init.mode=never

spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=20000

logging.level.com.zaxxer.hikari=INFO
```

As tabelas são criadas previamente utilizando os scripts SQL do projeto. A aplicação não depende de geração automática de tabelas pelo Hibernate.

### Variáveis de ambiente

Para utilizar o Oracle, configure localmente ou nas **Environment Variables** do Render:

```text
SPRING_PROFILES_ACTIVE=prod
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

Opcionalmente, configure `DB_URL` caso o endereço de conexão seja diferente. **Nunca publique senhas reais ou arquivos contendo credenciais no GitHub.**

## 🚀 Como executar localmente

1. Clone o repositório.
2. Abra o módulo `backend-java` no IntelliJ IDEA (localize o `pom.xml`).
3. Aguarde o carregamento das dependências Maven.
4. Configure as variáveis de ambiente do banco.
5. Execute a classe `AlertaCidadeApplication.java`.
6. Aguarde a inicialização do Spring Boot.

Quando a aplicação iniciar corretamente, serão exibidas mensagens semelhantes a:

```text
The following 1 profile is active: "prod"
Tomcat started on port 8080 (http)
Started AlertaCidadeApplication
```

A API local estará disponível em `http://localhost:8080`.

## 🌐 API REST

O projeto possui recursos relacionados a **alertas, pontos de monitoramento, sensores e medições**. Consulte as rotas e métodos disponíveis na documentação Swagger/OpenAPI, quando habilitada:

- Local: http://localhost:8080/swagger-ui.html
- Produção: https://alertacidade.onrender.com/swagger-ui.html

### GET — Consultar alertas

```http
GET http://localhost:8080/api/alertas
```

**Em produção:** `GET https://alertacidade.onrender.com/api/alertas`

Retorna os alertas cadastrados no sistema em formato JSON. A consulta foi verificada no serviço publicado no Render.

### POST — Criar alerta

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

### Resposta obtida

Durante os testes locais no Postman, a aplicação retornou **HTTP 201 Created**:

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

A resposta confirmou o processamento da requisição e a criação do alerta.

## ✅ Comprovação da integração com Oracle FIAP

Após a criação do alerta pelo Postman, foi realizada uma consulta diretamente no Oracle SQL Developer:

```sql
SELECT *
FROM T_AC_ALERTA
WHERE ID_ALERTA = 16;
```

### Resultado da consulta

| Campo | Valor |
|---|---|
| ID_ALERTA | 16 |
| ID_PONTO | 13 |
| ID_MEDICAO | NULL |
| NV_ALERTA | ATENCAO |
| DS_MENSAGEM | Teste de integracao Java com Oracle FIAP |
| ST_ALERTA | ABERTO |
| DT_ENCERRAMENTO | NULL |

A consulta retornou o mesmo registro criado pela API.

### Fluxo comprovado

```text
POSTMAN
   |
   | POST /api/alertas
   v
SPRING BOOT
   |
   | Processamento
   v
JDBC
   |
   | Persistência
   v
ORACLE FIAP
   |
   | SELECT WHERE ID_ALERTA = 16
   v
REGISTRO CONFIRMADO
```

Esse procedimento comprovou o funcionamento da integração entre o backend Java e o banco de dados Oracle FIAP. Adicionalmente, os logs do Render registraram a abertura de conexão JDBC com Oracle durante a execução em produção.

## 📸 Evidências dos testes

As verificações foram realizadas utilizando IntelliJ IDEA, Postman, navegador, Oracle SQL Developer e Render.

1. Inicialização do Spring Boot com o perfil `prod`.
2. Consulta das tabelas e contagem de registros no Oracle.
3. Consulta GET dos alertas pela API.
4. Requisição POST realizada pelo Postman.
5. Resposta HTTP `201 Created`.
6. Consulta SQL confirmando o alerta ID 16.
7. Deploy concluído no Render com status **Live**.
8. Logs do Render mostrando inicialização do Tomcat e conexão Oracle via HikariCP.
9. GET público em `/api/alertas` retornando JSON.

As capturas e explicações estão reunidas no relatório **AlertaCidade — Comprovação da Integração Oracle FIAP**, atualizado com o deploy.

## 🔐 Segurança

O projeto utiliza variáveis de ambiente para proteger as credenciais de conexão com o banco.

- Não publicar senhas no repositório.
- Não versionar arquivos `.env` com credenciais.
- Utilizar configurações específicas para desenvolvimento e produção.
- Evitar divulgar dados sensíveis em capturas de tela.
- Manter as dependências atualizadas.

## 📊 Resultados alcançados

- Backend Spring Boot inicializado com sucesso.
- Perfil de produção ativado.
- Integração com Oracle FIAP validada.
- Consultas SQL executadas com sucesso.
- Endpoint GET retornando alertas.
- Endpoint POST criando um alerta.
- Resposta HTTP `201 Created` confirmada.
- Alerta ID 16 localizado no banco Oracle.
- Deploy no Render concluído com sucesso.
- API pública acessível em `https://alertacidade.onrender.com/api/alertas`.
- Logs do Render confirmando conexão JDBC Oracle.

## 🔮 Próximas melhorias

- Dashboard web para acompanhamento dos alertas.
- Mapa interativo dos pontos de monitoramento.
- Integração com sensores IoT.
- Atualização automática de medições.
- Notificações de situações críticas.
- Relatórios e gráficos históricos.
- Ampliação dos testes automatizados.
- Melhorias de autenticação e controle de acesso.

## 🎓 Informações acadêmicas

**Instituição:** FIAP — Faculdade de Informática e Administração Paulista  
**Projeto:** AlertaCidade  
**Global Solution:** 2º semestre de 2026  
**Tema:** CitizenTech  
**Desenvolvedor:** Eduardo Antonio Delarissia  
**RM:** 563468

## 🏁 Conclusão

O AlertaCidade demonstrou o funcionamento de sua API REST integrada ao banco de dados Oracle FIAP.

O teste realizado com o Postman criou um novo alerta, retornando HTTP `201 Created`. Posteriormente, o mesmo registro foi localizado diretamente no Oracle SQL Developer.

Além da validação local, o backend foi publicado no Render com sucesso. A API pública retornou alertas em JSON, e os logs de produção registraram a inicialização do pool de conexões Oracle.

Essa validação confirma a comunicação e a persistência de dados entre Java, Spring Boot e Oracle, representando uma etapa importante do desenvolvimento do projeto.

**AlertaCidade — Tecnologia para cidades mais seguras.**
