-- ============================================================================
-- Flyway Migration: V1__initial_schema.sql
-- Description: Criação inicial do schema do banco de dados
-- Database: PostgreSQL
-- ============================================================================

-- ============================================================================
-- Tabela: users
-- Descrição: Armazena informações dos usuários do sistema
-- ============================================================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================================
-- Tabela: bank_accounts
-- Descrição: Armazena contas bancárias dos usuários
-- ============================================================================
CREATE TABLE bank_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    initial_balance NUMERIC(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('CHECKING', 'INVESTMENT', 'CASH')),
    color VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_bank_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- Tabela: categories
-- Descrição: Armazena categorias de transações dos usuários
-- ============================================================================
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- Tabela: transactions
-- Descrição: Armazena transações financeiras dos usuários
-- ============================================================================
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    bank_account_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    date DATE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- ============================================================================
-- Tabela: savings_goals
-- Descrição: Armazena metas de economia dos usuários
-- ============================================================================
CREATE TABLE savings_goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    target_amount NUMERIC(15,2) NOT NULL,
    current_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_savings_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
