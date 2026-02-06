# 🔧 Backend - API REST

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Status](https://img.shields.io/badge/Status-Funcional-success?style=for-the-badge)

</div>

---

## ✅ Status do Projeto

**Backend Funcional com Autenticação Completa**

O backend está totalmente operacional com sistema de autenticação JWT, gerenciamento de usuários, contas bancárias e categorias. Todas as funcionalidades principais estão implementadas e testadas.

---

## 🎯 Funcionalidades Chave

### 🔐 Autenticação e Segurança

- **Cadastro Seguro**: Senhas criptografadas com BCrypt antes de serem persistidas
- **Autenticação JWT**: Tokens com expiração de 14 dias para sessões seguras
- **Isolamento de Dados**: Todas as operações são isoladas por usuário autenticado
- **Validação de Entrada**: Bean Validation (JSR-303) em todos os endpoints
- **Tratamento de Exceções**: Handler global com respostas padronizadas

### 💳 Gestão de Contas Bancárias

- **CRUD Completo**: Criar, listar, atualizar e excluir contas bancárias
- **Tipos de Conta**: Suporte para CHECKING, INVESTMENT e CASH
- **Isolamento por Usuário**: Cada usuário vê apenas suas próprias contas
- **Personalização**: Nome, saldo inicial, tipo e cor para identificação visual

### 📂 Categorias

- **Seed Automático**: Categorias pré-configuradas criadas via Flyway
- **Tipos de Categoria**: INCOME (Receitas) e EXPENSE (Despesas)
- **Isolamento por Usuário**: Categorias personalizadas por usuário
- **Ícones**: Suporte para identificação visual com ícones

### 👥 Gestão de Usuários

- **Cadastro**: Registro de novos usuários com validação
- **Validação de Email**: Prevenção de emails duplicados
- **Proteção de Dados**: Senhas nunca expostas em respostas JSON

### 🗄️ Migrações de Banco de Dados

- **Flyway**: Versionamento automático do schema
- **Migrações Automáticas**: Executadas na inicialização da aplicação
- **Histórico Completo**: Controle de versão do banco de dados

---

## 🛠️ Tech Stack

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-brightgreen?style=for-the-badge&logo=spring-security)
![JWT](https://img.shields.io/badge/JWT-0.13.0-black?style=for-the-badge&logo=jsonwebtokens)
![BCrypt](https://img.shields.io/badge/BCrypt-10_rounds-blue?style=for-the-badge)
![Flyway](https://img.shields.io/badge/Flyway-10.x-red?style=for-the-badge&logo=flyway)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=for-the-badge&logo=apache-maven)
![Lombok](https://img.shields.io/badge/Lombok-1.18+-pink?style=for-the-badge)

---

## 📡 Endpoints Disponíveis

### 🔓 Endpoints Públicos

#### Autenticação
- `POST /api/auth/login` - Realizar login e obter token JWT
- `POST /api/users` - Registrar novo usuário

### 🔒 Endpoints Protegidos (Requerem JWT)

#### Contas Bancárias
- `GET /api/accounts` - Listar todas as contas do usuário autenticado
- `POST /api/accounts` - Criar nova conta bancária
- `PUT /api/accounts/{id}` - Atualizar conta bancária
- `DELETE /api/accounts/{id}` - Excluir conta bancária

#### Categorias
- `GET /api/categories` - Listar todas as categorias do usuário autenticado

---

## ⚙️ Configuração

### Pré-requisitos

- **Java 25** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+** instalado e rodando
- Arquivo `.env` configurado

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

### Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:
```sql
CREATE DATABASE nome_do_banco;
```

2. As migrações Flyway serão executadas automaticamente na primeira inicialização da aplicação

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

## 🧪 Testando a API

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
  "createdAt": "2026-02-06T10:00:00Z",
  "updatedAt": "2026-02-06T10:00:00Z"
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
  "user": {
    "id": "uuid-do-usuario",
    "name": "João Silva",
    "email": "joao@example.com"
  }
}
```

### 3. Criar uma Conta Bancária (Requer Autenticação)

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

### 4. Listar Contas Bancárias (Requer Autenticação)

```bash
curl -X GET http://localhost:8080/api/accounts \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

### 5. Listar Categorias (Requer Autenticação)

```bash
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

---

## 📁 Estrutura do Projeto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/astrocode/backend/
│   │   │   ├── api/
│   │   │   │   ├── controllers/     # Controllers REST
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── BankAccountController.java
│   │   │   │   │   ├── CategoryController.java
│   │   │   │   │   └── UserController.java
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   └── exception/       # Exception handlers
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   ├── config/              # Configurações
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── domain/
│   │   │   │   ├── entities/        # Entidades JPA
│   │   │   │   ├── exceptions/      # Exceções de domínio
│   │   │   │   ├── model/           # Enums
│   │   │   │   ├── repositories/    # Repositórios JPA
│   │   │   │   └── services/        # Lógica de negócio
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/        # Migrações Flyway
│   │           ├── V1__initial_schema.sql
│   │           └── V2__add_type_to_categories.sql
│   └── test/                         # Testes unitários
├── .env.example                      # Exemplo de variáveis de ambiente
└── pom.xml                           # Dependências Maven
```

---

## 🔒 Segurança

### Implementações de Segurança

- ✅ **BCrypt**: Senhas criptografadas com custo padrão de 10 rounds
- ✅ **JWT**: Tokens assinados com HMAC SHA-256
- ✅ **Spring Security**: Configuração de segurança para APIs REST
- ✅ **CORS**: Configurado para permitir requisições do frontend
- ✅ **Validação**: Validação de entrada em todos os endpoints
- ✅ **Isolamento**: Dados isolados por usuário autenticado
- ✅ **Proteção de Dados**: Senhas nunca retornadas em respostas JSON

### Configuração de CORS

O backend está configurado para aceitar requisições de:
- `http://localhost:3000`
- `http://localhost:5173`

---

## 📊 Banco de Dados

### Schema Principal

- **users**: Usuários do sistema
- **bank_accounts**: Contas bancárias dos usuários
- **categories**: Categorias de transações
- **transactions**: Transações financeiras
- **savings_goals**: Metas de economia

### Migrações Flyway

As migrações são executadas automaticamente na inicialização:
- `V1__initial_schema.sql`: Schema inicial com todas as tabelas
- `V2__add_type_to_categories.sql`: Adiciona coluna `type` nas categorias

---

## 🐛 Tratamento de Erros

A API retorna erros padronizados em formato JSON:

```json
{
  "timestamp": "2026-02-06T10:00:00Z",
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

---

## 📝 Licença

Este projeto está sob a licença especificada no arquivo [LICENSE](../LICENSE).

---

<div align="center">

**Desenvolvido com Spring Boot 4.0.2 e Java 25**

</div>
