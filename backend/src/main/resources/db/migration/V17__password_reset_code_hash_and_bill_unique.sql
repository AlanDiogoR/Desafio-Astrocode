ALTER TABLE password_reset_codes ALTER COLUMN code TYPE VARCHAR(64);

ALTER TABLE savings_goals DROP CONSTRAINT IF EXISTS savings_goals_status_check;
ALTER TABLE savings_goals ADD CONSTRAINT savings_goals_status_check CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED'));
