-- ============================================================================
-- Flyway Migration: V4__add_color_to_savings_goals.sql
-- Description: Adiciona coluna color à tabela savings_goals
-- Database: PostgreSQL
-- ============================================================================

ALTER TABLE savings_goals
ADD COLUMN color VARCHAR(30);
