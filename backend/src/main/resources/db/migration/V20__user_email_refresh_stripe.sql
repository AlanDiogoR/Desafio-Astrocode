-- Verificação de e-mail, refresh token (hash) e Stripe customer id
ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;
UPDATE users SET email_verified = TRUE;
ALTER TABLE users ADD COLUMN email_verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN refresh_token_hash VARCHAR(64);
ALTER TABLE users ADD COLUMN stripe_customer_id VARCHAR(255);

CREATE UNIQUE INDEX idx_users_email_verification_token
    ON users(email_verification_token) WHERE email_verification_token IS NOT NULL;
