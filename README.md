# Sistema de Controle Financeiro (DMF)

Sistema de controle financeiro pessoal desenvolvido com arquitetura limpa e boas práticas de segurança.

## Tech Stack

- **Spring Boot** 4.0.2
- **Java** 25
- **PostgreSQL** - Banco de dados relacional
- **JPA/Hibernate** - ORM para persistência de dados
- **Flyway** - Migrações de banco de dados automatizadas
- **Spring Security** - Segurança e autenticação
- **BCrypt** - Criptografia de senhas
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências

## Database Schema

O banco de dados é gerenciado através de migrações Flyway. O schema inicial inclui as seguintes tabelas:

### Tabela: `users`
Armazena informações dos usuários do sistema.
- `id` (UUID) - Identificador único
- `name` (VARCHAR 100) - Nome do usuário
- `email` (VARCHAR 150) - Email único do usuário
- `password` (VARCHAR 255) - Senha criptografada com BCrypt
- `created_at` (TIMESTAMPTZ) - Data de criação
- `updated_at` (TIMESTAMPTZ) - Data de última atualização

### Tabela: `bank_accounts`
Armazena contas bancárias dos usuários.
- `id` (UUID) - Identificador único
- `user_id` (UUID) - Referência ao usuário (FK)
- `name` (VARCHAR 100) - Nome da conta
- `initial_balance` (NUMERIC 15,2) - Saldo inicial
- `type` (VARCHAR 20) - Tipo da conta: CHECKING, INVESTMENT ou CASH
- `color` (VARCHAR 30) - Cor para identificação visual
- `created_at` (TIMESTAMPTZ) - Data de criação
- `updated_at` (TIMESTAMPTZ) - Data de última atualização

### Tabela: `categories`
Armazena categorias de transações dos usuários.
- `id` (UUID) - Identificador único
- `user_id` (UUID) - Referência ao usuário (FK)
- `name` (VARCHAR 100) - Nome da categoria
- `icon` (VARCHAR 50) - Ícone da categoria
- `created_at` (TIMESTAMPTZ) - Data de criação
- `updated_at` (TIMESTAMPTZ) - Data de última atualização

### Tabela: `transactions`
Armazena transações financeiras dos usuários.
- `id` (UUID) - Identificador único
- `user_id` (UUID) - Referência ao usuário (FK)
- `bank_account_id` (UUID) - Referência à conta bancária (FK)
- `category_id` (UUID) - Referência à categoria (FK)
- `name` (VARCHAR 150) - Nome/descrição da transação
- `amount` (NUMERIC 15,2) - Valor da transação
- `date` (DATE) - Data da transação
- `type` (VARCHAR 20) - Tipo: INCOME ou EXPENSE
- `created_at` (TIMESTAMPTZ) - Data de criação
- `updated_at` (TIMESTAMPTZ) - Data de última atualização

### Tabela: `savings_goals`
Armazena metas de economia dos usuários.
- `id` (UUID) - Identificador único
- `user_id` (UUID) - Referência ao usuário (FK)
- `name` (VARCHAR 120) - Nome da meta
- `target_amount` (NUMERIC 15,2) - Valor alvo
- `current_amount` (NUMERIC 15,2) - Valor atual acumulado
- `start_date` (DATE) - Data de início
- `end_date` (DATE) - Data de término (opcional)
- `status` (VARCHAR 20) - Status: ACTIVE, COMPLETED ou CANCELLED
- `created_at` (TIMESTAMPTZ) - Data de criação
- `updated_at` (TIMESTAMPTZ) - Data de última atualização

### Relacionamentos
- Todas as tabelas relacionadas a usuários possuem `ON DELETE CASCADE`, garantindo que ao excluir um usuário, todos os seus dados relacionados sejam removidos automaticamente.
- Foreign keys garantem integridade referencial entre as entidades.

## Features Implemented

### Segurança
- **Criptografia de Senhas**: Senhas são criptografadas usando BCrypt antes de serem persistidas no banco de dados
- **Proteção de Dados Sensíveis**: Senhas nunca são retornadas em respostas JSON (usando `@JsonIgnore`)
- **Spring Security**: Configurado para permitir acesso público ao endpoint de registro

### Migrações de Banco de Dados
- **Flyway**: Migrações automatizadas garantem versionamento e controle do schema do banco
- Migrações são executadas automaticamente na inicialização da aplicação

### Validação de Dados
- Validação de entrada usando Bean Validation (JSR-303)
- Validação de email, tamanho de campos e campos obrigatórios
- Mensagens de erro padronizadas e informativas

### Tratamento de Exceções
- Handler global de exceções (`GlobalExceptionHandler`)
- Respostas de erro padronizadas em formato JSON
- Tratamento específico para conflitos (email duplicado) e erros de validação

### Arquitetura
- Arquitetura em camadas (Controllers, Services, Repositories)
- Separação de responsabilidades
- Uso de DTOs para transferência de dados
- Entidades JPA com relacionamentos configurados

## How to Run

### Pré-requisitos

- **Java 25** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+** instalado e rodando
- Arquivo `.env` configurado (veja `.env.example`)

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/AlanDiogoR/Desafio-Astrocode.git
cd dmf
```

2. Configure o banco de dados PostgreSQL criando um banco de dados:
```sql
CREATE DATABASE seu_banco_de_dados;
```

3. Crie o arquivo `.env` na pasta `backend/` baseado no `.env.example`:
```bash
cd backend
cp .env.example .env
```

4. Edite o arquivo `.env` com suas credenciais:
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=seu_banco_de_dados
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
```

### Executando a Aplicação

1. Navegue até a pasta `backend/`:
```bash
cd backend
```

2. Execute a aplicação usando Maven:
```bash
mvn spring-boot:run
```

Ou compile e execute:
```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Variáveis de Ambiente Necessárias

O projeto utiliza as seguintes variáveis de ambiente (configuradas no arquivo `.env`):

- `DB_HOST` - Host do banco de dados PostgreSQL
- `DB_PORT` - Porta do banco de dados (padrão: 5432)
- `DB_NAME` - Nome do banco de dados
- `DB_USERNAME` - Usuário do banco de dados
- `DB_PASSWORD` - Senha do banco de dados

### Testando o Endpoint

Após iniciar a aplicação, você pode testar o endpoint de cadastro de usuários:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

A resposta será um JSON com os dados do usuário criado (sem a senha):

```json
{
  "id": "uuid-do-usuario",
  "name": "João Silva",
  "email": "joao@example.com",
  "createdAt": "2026-02-04T10:00:00Z",
  "updatedAt": "2026-02-04T10:00:00Z"
}
```

## Estrutura do Projeto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/astrocode/backend/
│   │   │   ├── api/
│   │   │   │   ├── controllers/     # Controllers REST
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   └── exception/       # Exception handlers
│   │   │   ├── config/              # Configurações (Security, etc)
│   │   │   ├── domain/
│   │   │   │   ├── entities/        # Entidades JPA
│   │   │   │   ├── exceptions/      # Exceções de domínio
│   │   │   │   ├── model/           # Enums e modelos
│   │   │   │   ├── repositories/    # Repositórios JPA
│   │   │   │   └── services/        # Lógica de negócio
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/        # Migrações Flyway
│   └── test/                         # Testes unitários e de integração
└── pom.xml
```

## Segurança

- Senhas são criptografadas com BCrypt (custo padrão: 10 rounds)
- Senhas nunca são expostas em respostas JSON
- Validação de entrada para prevenir ataques de injeção
- Spring Security configurado para APIs REST

## Licença

Este projeto está sob a licença especificada no arquivo LICENSE.
