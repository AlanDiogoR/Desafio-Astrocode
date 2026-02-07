-- ============================================================================
-- Flyway Migration: V3__add_current_balance_to_bank_accounts.sql
-- Description: Adiciona coluna current_balance na tabela bank_accounts para armazenar saldo atual
-- Database: PostgreSQL
-- ============================================================================

-- Adiciona coluna current_balance na tabela bank_accounts
ALTER TABLE bank_accounts
ADD COLUMN current_balance NUMERIC(15,2) NOT NULL DEFAULT 0;

-- Inicializa current_balance com o valor de initial_balance para registros existentes
UPDATE bank_accounts
SET current_balance = initial_balance;

-- Remove o valor padrão após inicializar os dados existentes
ALTER TABLE bank_accounts
ALTER COLUMN current_balance DROP DEFAULT;
