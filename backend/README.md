# 🔧 Backend - API REST

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Status](https://img.shields.io/badge/Status-Funcional-success?style=for-the-badge)

</div>

---

## ✅ Status do Projeto

**Backend Funcional com Sistema Financeiro Completo**

O backend está totalmente operacional com sistema de autenticação JWT, gerenciamento completo de usuários, contas bancárias, transações financeiras, metas de poupança e dashboard de resumo. Todas as funcionalidades principais estão implementadas, testadas e prontas para produção.

---

## 🛠️ Tech Stack

### Tecnologias Principais

- **Java 21** - Linguagem de programação com suporte a Records (Java 14+) e novas funcionalidades de performance
- **Spring Boot 4.0.2** - Framework principal para desenvolvimento de aplicações Java
- **Spring Security 6.x** - Framework de segurança para autenticação e autorização
- **Spring Data JPA** - Abstração para acesso a dados com Hibernate
- **JWT 0.13.0** - Autenticação stateless com tokens JSON Web Token
- **PostgreSQL 16** - Banco de dados relacional
- **Flyway** - Versionamento e migração automática do banco de dados
- **Lombok** - Redução de boilerplate com anotações
- **Maven 3.6+** - Gerenciamento de dependências e build

### Características Técnicas

- **Records Java**: Utilização de Records para DTOs, garantindo imutabilidade e código mais conciso
- **Bean Validation**: Validação de entrada em todos os endpoints com Jakarta Validation
- **BCrypt**: Criptografia de senhas com custo de 10 rounds
- **JWT Stateless**: Tokens com expiração configurável (padrão: 14 dias)
- **Migrações Automáticas**: Flyway executa migrações na inicialização da aplicação

---

## 🎯 Funcionalidades Implementadas

### 🔐 Autenticação & Segurança

- **JWT Stateless**: Autenticação sem estado com tokens de 14 dias de expiração
- **BCrypt para Senhas**: Criptografia robusta antes da persistência
- **Isolamento de Dados**: Todas as operações são isoladas por usuário autenticado
- **Validação de Entrada**: Bean Validation (JSR-303) em todos os endpoints
- **Tratamento de Exceções**: Handler global com respostas padronizadas em JSON
- **Proteção de Dados**: Senhas nunca expostas em respostas JSON

### 💳 Gestão de Contas Bancárias

- **CRUD Completo**: Criar, listar, atualizar e excluir contas bancárias
- **Tipos de Conta**: Suporte para CHECKING (Conta Corrente), INVESTMENT (Investimento) e CASH (Dinheiro)
- **Isolamento por Usuário**: Cada usuário vê apenas suas próprias contas
- **Reconciliação Automática**: Saldo atualizado automaticamente ao criar/editar/deletar transações
- **Personalização**: Nome, saldo inicial, tipo e cor para identificação visual

### 💸 Transações Financeiras

- **Lógica de Entradas e Saídas**: Suporte para INCOME (Receitas) e EXPENSE (Despesas)
- **Reconciliação Bancária Automática**: 
  - Saldo da conta atualiza automaticamente ao criar transação
  - Reversão e recálculo ao editar transação existente
  - Reversão ao deletar transação
- **Filtros Avançados**: 
  - Por mês e ano (`?year=2026&month=2`)
  - Por conta bancária (`?bankAccountId=uuid`)
  - Por tipo (`?type=INCOME` ou `?type=EXPENSE`)
  - Combinação de filtros suportada
- **Validações de Negócio**:
  - Validação de tipo de categoria vs tipo de transação
  - Validação de saldo insuficiente para despesas
  - Validação de propriedade de conta e categoria pelo usuário
- **Transações Recorrentes**:
  - Marcação de transações como recorrentes mensal
  - Job agendado diário (00:05) gera automaticamente as transações do período atual
  - Transações filhas vinculadas ao pai via `parent_transaction_id`
  - Evita duplicatas verificando se já existe filha para o mês/ano

### 🎯 Metas de Poupança (Savings Goals)

- **CRUD Completo**: Criar, listar, atualizar e excluir metas
- **Cálculo Automático de Progresso**: Percentual calculado automaticamente (currentAmount / targetAmount * 100)
- **Contribuir**: Endpoint PATCH `/api/goals/{id}/contribute` - transfere valor da conta bancária para a meta
- **Sacar**: Endpoint PATCH `/api/goals/{id}/withdraw` - transfere valor da meta de volta para a conta
- **Status Tracking**: Suporte para ACTIVE, COMPLETED e CANCELLED
- **Personalização**: Nome, valor alvo, cor, data fim e acompanhamento de progresso

### 📊 Dashboard

- **Resumo Financeiro Consolidado**: Endpoint único com dados agregados
- **Saldo Total**: Soma de todos os saldos das contas do usuário
- **Totais Mensais**: 
  - Total de receitas do mês atual
  - Total de despesas do mês atual
- **Performance Otimizada**: Consultas agregadas no banco de dados

### 📂 Categorias

- **Seed Automático**: Categorias pré-configuradas criadas automaticamente no cadastro do usuário
- **Tipos de Categoria**: INCOME (Receitas) e EXPENSE (Despesas)
- **Isolamento por Usuário**: Categorias personalizadas por usuário
- **Ícones**: Suporte para identificação visual com ícones
- **Validação de Tipo**: Categoria deve corresponder ao tipo da transação

### 👥 Gestão de Usuários

- **Cadastro**: Registro de novos usuários com validação completa
- **Validação de Email**: Prevenção de emails duplicados
- **Criação de Categorias Padrão**: Ao cadastrar, usuário recebe categorias pré-configuradas

---

## 🏗️ Arquitetura do Projeto

### Organização por Camadas

O projeto segue uma arquitetura em camadas bem definida:

- **Controllers** (`api/controllers/`): Endpoints REST, validação de entrada e formatação de saída
- **Services** (`domain/services/`): Lógica de negócio e orquestração
- **Repositories** (`domain/repositories/`): Acesso a dados com Spring Data JPA
- **Entities** (`domain/entities/`): Entidades JPA representando o modelo de domínio
- **DTOs** (`api/dto/`): Objetos de transferência de dados organizados por domínio

### Organização de DTOs por Domínio

Os DTOs foram refatorados e organizados em subpacotes por domínio de negócio para melhor manutenibilidade e escalabilidade:

```
api/dto/
├── auth/          # Autenticação
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── user/          # Usuários
│   ├── UserRegistrationRequest.java
│   └── UserResponse.java
├── account/       # Contas bancárias
│   ├── BankAccountRequest.java
│   └── BankAccountResponse.java
├── category/      # Categorias
│   └── CategoryResponse.java
├── transaction/   # Transações
│   ├── TransactionRequest.java
│   ├── TransactionResponse.java
│   └── TransactionUpdateRequest.java
├── goal/          # Metas de poupança
│   ├── SavingsGoalRequest.java
│   ├── SavingsGoalResponse.java
│   ├── SavingsGoalContributeRequest.java
│   └── SavingsGoalWithdrawRequest.java
└── dashboard/     # Dashboard
    └── DashboardResponse.java
```

**Benefícios da Organização por Domínio:**
- ✅ Melhor manutenibilidade: DTOs relacionados agrupados logicamente
- ✅ Escalabilidade: Fácil adicionar novos DTOs sem poluir o pacote raiz
- ✅ Clareza: Estrutura reflete a organização do domínio de negócio
- ✅ Reutilização: Imports mais claros e organizados

---

## 📡 Diagrama de Endpoints

```
POST   /api/auth/login          → JWT
POST   /api/users               → Cadastro

GET    /api/accounts            → Lista contas
POST   /api/accounts            → Cria conta
PUT    /api/accounts/{id}       → Atualiza conta
DELETE /api/accounts/{id}       → Exclui conta

GET    /api/categories          → Lista categorias

GET    /api/transactions        → Lista transações (?year, ?month, ?bankAccountId, ?type)
POST   /api/transactions        → Cria transação
PUT    /api/transactions/{id}   → Atualiza transação
DELETE /api/transactions/{id}   → Exclui transação

GET    /api/goals               → Lista metas
POST   /api/goals               → Cria meta
PUT    /api/goals/{id}          → Atualiza meta
PATCH  /api/goals/{id}/contribute → Contribui para meta
PATCH  /api/goals/{id}/withdraw   → Saca da meta
DELETE /api/goals/{id}          → Exclui meta

GET    /api/dashboard           → Resumo (saldo, receitas/despesas do mês)
```

---

## 📡 Guia de Endpoints

### 🔓 Endpoints Públicos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/auth/login` | Autenticação e obtenção de token JWT |
| `POST` | `/api/users` | Registro de novo usuário |

### 🔒 Endpoints Protegidos (Requerem JWT)

#### Contas Bancárias

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/accounts` | Listar todas as contas do usuário autenticado |
| `POST` | `/api/accounts` | Criar nova conta bancária |
| `PUT` | `/api/accounts/{id}` | Atualizar conta bancária |
| `DELETE` | `/api/accounts/{id}` | Excluir conta bancária |

#### Categorias

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/categories` | Listar todas as categorias do usuário autenticado |

#### Transações Financeiras

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/transactions` | Listar transações com filtros opcionais |
| `POST` | `/api/transactions` | Criar nova transação |
| `PUT` | `/api/transactions/{id}` | Atualizar transação existente |
| `DELETE` | `/api/transactions/{id}` | Excluir transação |

**Parâmetros de Filtro para GET /api/transactions:**
- `year` (Integer): Filtrar por ano (ex: `?year=2026`)
- `month` (Integer): Filtrar por mês (ex: `?month=2`)
- `bankAccountId` (UUID): Filtrar por conta bancária
- `type` (TransactionType): Filtrar por tipo (`INCOME` ou `EXPENSE`)

**Exemplo de uso combinado:**
```
GET /api/transactions?year=2026&month=2&type=EXPENSE&bankAccountId=uuid-da-conta
```

#### Metas de Poupança

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/goals` | Listar todas as metas do usuário |
| `POST` | `/api/goals` | Criar nova meta de poupança |
| `PUT` | `/api/goals/{id}` | Atualizar meta completa |
| `PATCH` | `/api/goals/{id}/contribute` | Contribuir para a meta (débito da conta + crédito na meta) |
| `PATCH` | `/api/goals/{id}/withdraw` | Sacar da meta (crédito na conta + débito na meta) |
| `DELETE` | `/api/goals/{id}` | Excluir meta |

#### Dashboard

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/dashboard` | Obter resumo financeiro consolidado |

---

## ⚙️ Configuração

### Pré-requisitos

- **Java 21** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+** instalado e rodando
- Arquivo `.env` configurado na pasta `backend/`

### Variáveis de Ambiente

Crie um arquivo `.env` na pasta `backend/` baseado no `.env.example`:

```properties
# Database Configuration
DB_HOST=seu-host-postgresql
DB_PORT=5432
DB_NAME=nome-do-banco
DB_USERNAME=seu-usuario
DB_PASSWORD=sua-senha

# JWT Configuration
JWT_SECRET=uma_chave_segura_com_pelo_menos_32_caracteres_aleatorios
```

**⚠️ Importante**: 
- O `JWT_SECRET` deve ter pelo menos 32 caracteres para segurança adequada
- Nunca commite o arquivo `.env` no repositório (já está no `.gitignore`)

**☁️ Configuração em Serviços de Cloud**:
Ao fazer deploy em serviços de Cloud (como Render, Railway ou Heroku), configure as variáveis de ambiente diretamente no painel do serviço. Não é necessário criar o arquivo `.env` manualmente - as variáveis de ambiente configuradas no serviço serão automaticamente utilizadas pela aplicação.

**📧 Recuperação de senha (Railway)**:
Use **Brevo** (API HTTP, funciona sem domínio):
1. Crie conta em [brevo.com](https://www.brevo.com)
2. Configurações > Remetentes > Adicione e verifique o e-mail (ex: grivycontrolefinanceiro@gmail.com)
3. Configurações > API Keys > Gere uma chave
4. No Railway: `BREVO_API_KEY=xkeysib_xxx` e `MAIL_FROM=grivycontrolefinanceiro@gmail.com`

### Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:
```sql
CREATE DATABASE nome_do_banco;
```

2. **Migrações Flyway**: As migrações serão executadas automaticamente na inicialização da aplicação. Não é necessário executar comandos manuais.

---

## 🚀 Como Executar

### Opção 1: Maven Spring Boot Plugin

```bash
cd backend
mvn spring-boot:run
```

### Opção 2: Compilar e Executar JAR

```bash
cd backend
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Opção 3: Executar com Maven Wrapper

```bash
cd backend
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 🧪 Exemplos de Uso da API

### 1. Registrar um Usuário

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

**Resposta:**
```json
{
  "id": "uuid-do-usuario",
  "name": "João Silva",
  "email": "joao@example.com",
  "createdAt": "2026-02-08T10:00:00Z",
  "updatedAt": "2026-02-08T10:00:00Z"
}
```

### 2. Fazer Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "João Silva"
}
```

### 3. Criar uma Conta Bancária

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -d '{
    "name": "Conta Corrente",
    "initialBalance": 1000.00,
    "type": "CHECKING",
    "color": "#3B82F6"
  }'
```

### 4. Criar uma Transação

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -d '{
    "name": "Salário",
    "amount": 5000.00,
    "date": "2026-02-01",
    "type": "INCOME",
    "bankAccountId": "uuid-da-conta",
    "categoryId": "uuid-da-categoria"
  }'
```

**Nota**: O saldo da conta será atualizado automaticamente após criar a transação.

### 5. Listar Transações com Filtros

```bash
# Listar todas as despesas de fevereiro de 2026
curl -X GET "http://localhost:8080/api/transactions?year=2026&month=2&type=EXPENSE" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"

# Listar transações de uma conta específica
curl -X GET "http://localhost:8080/api/transactions?bankAccountId=uuid-da-conta" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

### 6. Criar uma Meta de Poupança

```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -d '{
    "name": "Viagem para Europa",
    "targetAmount": 10000.00,
    "color": "#10B981"
  }'
```

**Resposta:**
```json
{
  "id": "uuid-da-meta",
  "name": "Viagem para Europa",
  "targetAmount": 10000.00,
  "currentAmount": 0.00,
  "color": "#10B981",
  "progressPercentage": 0.00,
  "status": "ACTIVE",
  "createdAt": "2026-02-08T10:00:00Z",
  "updatedAt": "2026-02-08T10:00:00Z"
}
```

### 7. Contribuir para uma Meta (PATCH)

```bash
curl -X PATCH http://localhost:8080/api/goals/uuid-da-meta/contribute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -d '{
    "amount": 2500.00,
    "bankAccountId": "uuid-da-conta-bancaria"
  }'
```

**Resposta:** O valor é debitado da conta bancária e creditado na meta. O percentual de progresso será recalculado automaticamente.

### 8. Sacar de uma Meta (PATCH)

```bash
curl -X PATCH http://localhost:8080/api/goals/uuid-da-meta/withdraw \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI" \
  -d '{
    "amount": 500.00,
    "bankAccountId": "uuid-da-conta-bancaria"
  }'
```

**Resposta:** O valor é debitado da meta e creditado na conta bancária.

### 9. Obter Dashboard

```bash
curl -X GET http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

**Resposta:**
```json
{
  "totalBalance": 15000.00,
  "totalIncomeMonth": 5000.00,
  "totalExpenseMonth": 1200.00
}
```

---

## 🚨 Troubleshooting

| Problema | Causa provável | Solução |
|----------|----------------|---------|
| **401 Unauthorized** | Token JWT inválido ou expirado | Refaça login. Verifique se o header é `Authorization: Bearer <token>`. |
| **Token inválido ao iniciar** | `JWT_SECRET` alterado ou diferente entre deploys | Use a mesma chave em todos os ambientes. Mínimo 32 caracteres. |
| **CORS bloqueando requisições** | Frontend em origem não permitida | Adicione a origem em `SecurityConfig` (allowedOrigins). |
| **Banco não conecta** | PostgreSQL indisponível ou credenciais incorretas | Verifique `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`. Teste conexão com `psql`. |
| **Flyway / migração falha** | Schema inconsistente ou migração antiga quebrada | Revise `db/migration/`. Em dev, pode ser necessário recriar o banco. |
| **Porta 8080 em uso** | Outro processo usando a porta | Altere `server.port` em `application.properties` ou mate o processo. |

---

## 📁 Estrutura do Projeto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/astrocode/backend/
│   │   │   ├── api/
│   │   │   │   ├── controllers/          # Controllers REST
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── BankAccountController.java
│   │   │   │   │   ├── CategoryController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   ├── SavingsGoalController.java
│   │   │   │   │   ├── TransactionController.java
│   │   │   │   │   └── UserController.java
│   │   │   │   ├── dto/                  # DTOs organizados por domínio
│   │   │   │   │   ├── account/          # Contas bancárias
│   │   │   │   │   ├── auth/             # Autenticação
│   │   │   │   │   ├── category/         # Categorias
│   │   │   │   │   ├── dashboard/        # Dashboard
│   │   │   │   │   ├── goal/             # Metas de poupança
│   │   │   │   │   ├── transaction/      # Transações
│   │   │   │   │   └── user/             # Usuários
│   │   │   │   └── exception/            # Exception handlers
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   ├── config/                   # Configurações
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── domain/
│   │   │   │   ├── entities/             # Entidades JPA
│   │   │   │   │   ├── BankAccount.java
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── SavingsGoal.java
│   │   │   │   │   ├── Transaction.java
│   │   │   │   │   └── User.java
│   │   │   │   ├── exceptions/           # Exceções de domínio
│   │   │   │   ├── model/                # Enums
│   │   │   │   │   └── enums/
│   │   │   │   │       ├── AccountType.java
│   │   │   │   │       ├── GoalStatus.java
│   │   │   │   │       └── TransactionType.java
│   │   │   │   ├── repositories/         # Repositórios JPA
│   │   │   │   └── services/            # Lógica de negócio
│   │   │   │       ├── AuthService.java
│   │   │   │       ├── BankAccountService.java
│   │   │   │       ├── CategoryService.java
│   │   │   │       ├── DashboardService.java
│   │   │   │       ├── JwtService.java
│   │   │   │       ├── SavingsGoalService.java
│   │   │   │       ├── TransactionService.java
│   │   │   │       └── UserService.java
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/             # Migrações Flyway
│   │           └── V1__initial_schema.sql
│   └── test/                              # Testes unitários
├── .env.example                           # Exemplo de variáveis de ambiente
└── pom.xml                                # Dependências Maven
```

---

## 🔒 Segurança

### Implementações de Segurança

- ✅ **BCrypt**: Senhas criptografadas com custo padrão de 10 rounds
- ✅ **JWT**: Tokens assinados com HMAC SHA-256, expiração de 14 dias
- ✅ **Spring Security**: Configuração de segurança para APIs REST
- ✅ **CORS**: Configurado para permitir requisições do frontend
- ✅ **Validação**: Validação de entrada em todos os endpoints com Bean Validation
- ✅ **Isolamento**: Dados isolados por usuário autenticado em todas as operações
- ✅ **Proteção de Dados**: Senhas nunca retornadas em respostas JSON

### Configuração de CORS

O backend está configurado para aceitar requisições de:
- `https://grivy.netlify.app` (produção)
- `https://www.grivy.netlify.app`
- `http://localhost:3000`
- `http://localhost:5173`

---

## 📊 Banco de Dados

### Schema Principal

- **users**: Usuários do sistema
- **bank_accounts**: Contas bancárias dos usuários
- **categories**: Categorias de transações (INCOME/EXPENSE)
- **transactions**: Transações financeiras com reconciliação automática
- **savings_goals**: Metas de economia com cálculo de progresso

### Migrações Flyway

As migrações são executadas **automaticamente** na inicialização da aplicação:
- `V1__initial_schema.sql`: Schema inicial com todas as tabelas e relacionamentos

**Nota**: Não é necessário executar comandos manuais do Flyway. A aplicação gerencia as migrações automaticamente.

---

## 🐛 Tratamento de Erros

A API retorna erros padronizados em formato JSON:

```json
{
  "timestamp": "2026-02-08T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Mensagem de erro descritiva",
  "path": "/api/endpoint"
}
```

### Códigos de Status HTTP

- `200 OK`: Requisição bem-sucedida
- `201 Created`: Recurso criado com sucesso
- `400 Bad Request`: Erro de validação ou requisição inválida
- `401 Unauthorized`: Token JWT inválido ou ausente
- `403 Forbidden`: Acesso negado ao recurso
- `404 Not Found`: Recurso não encontrado
- `409 Conflict`: Conflito (ex: email já cadastrado)
- `500 Internal Server Error`: Erro interno do servidor

### Exceções de Domínio

O projeto utiliza exceções customizadas para melhor tratamento de erros:
- `EmailAlreadyExistsException`: Email já cadastrado
- `InvalidCredentialsException`: Credenciais inválidas
- `InvalidTokenException`: Token JWT inválido ou expirado
- `ResourceNotFoundException`: Recurso não encontrado
- `ResourceAccessDeniedException`: Acesso negado ao recurso
- `AccountNotOwnedException`: Conta não pertence ao usuário
- `InsufficientBalanceException`: Saldo insuficiente
- `CategoryTypeMismatchException`: Tipo de categoria não corresponde ao tipo de transação

---

## 📝 Licença

Este projeto está sob a licença especificada no arquivo [LICENSE](../LICENSE).

---

<div align="center">

**Desenvolvido com Spring Boot 4.0.2 e Java 21**

</div>
