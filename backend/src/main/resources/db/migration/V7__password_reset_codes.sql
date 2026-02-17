-- ============================================================================
-- Flyway Migration: V7__password_reset_codes.sql
-- Description: Tabela de códigos de recuperação de senha
-- Database: PostgreSQL
-- ============================================================================

-- ============================================================================
-- Tabela: password_reset_codes
-- Descrição: Armazena códigos temporários para recuperação de senha
-- ============================================================================
CREATE TABLE password_reset_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(150) NOT NULL,
    code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_password_reset_codes_email ON password_reset_codes(email);
CREATE INDEX idx_password_reset_codes_expires_at ON password_reset_codes(expires_at);
