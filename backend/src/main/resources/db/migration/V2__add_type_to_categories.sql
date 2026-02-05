-- ============================================================================
-- Flyway Migration: V2__add_type_to_categories.sql
-- Description: Adiciona coluna type na tabela categories para diferenciar Receitas e Despesas
-- Database: PostgreSQL
-- ============================================================================

-- Adiciona coluna type na tabela categories
ALTER TABLE categories
ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'EXPENSE';

-- Adiciona constraint CHECK para validar valores permitidos
ALTER TABLE categories
ADD CONSTRAINT chk_categories_type CHECK (type IN ('INCOME', 'EXPENSE'));

-- Remove o valor padrão após adicionar a constraint (dados existentes já têm valor)
ALTER TABLE categories
ALTER COLUMN type DROP DEFAULT;
