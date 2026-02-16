-- ============================================================================
-- Flyway Migration: V6__transaction_recurrence_fields.sql
-- Description: Campos de recorrência em transações (fixas mensais/anuais)
-- Database: PostgreSQL
-- ============================================================================

-- Transações podem ser marcadas como recorrentes
ALTER TABLE transactions
ADD COLUMN is_recurring BOOLEAN NOT NULL DEFAULT false;

-- Frequência da recorrência: MONTHLY, YEARLY
ALTER TABLE transactions
ADD COLUMN frequency VARCHAR(20);

-- Auto-relacionamento para rastrear transações geradas a partir de uma recorrente
ALTER TABLE transactions
ADD COLUMN parent_transaction_id UUID;

-- FK: transação filha referencia a transação pai (ON DELETE SET NULL para manter histórico)
ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_parent FOREIGN KEY (parent_transaction_id) REFERENCES transactions(id) ON DELETE SET NULL;
