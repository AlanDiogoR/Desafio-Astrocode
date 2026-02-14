-- ============================================================================
-- Flyway Migration: V5__goals_soft_delete_and_transaction_goal.sql
-- Description: Soft delete em metas e vínculo transação-meta (aportes e resgates)
-- Database: PostgreSQL
-- ============================================================================

-- Soft delete: metas arquivadas, nunca removidas
ALTER TABLE savings_goals
ADD COLUMN deleted_at TIMESTAMPTZ;

-- Permite rastrear aportes/resgates na meta
ALTER TABLE transactions
ADD COLUMN goal_id UUID;

-- FK: transação pode referenciar uma meta (ON DELETE SET NULL para manter histórico)
ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_goal FOREIGN KEY (goal_id) REFERENCES savings_goals(id) ON DELETE SET NULL;
