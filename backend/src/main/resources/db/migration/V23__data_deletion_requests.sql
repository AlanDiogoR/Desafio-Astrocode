CREATE TABLE IF NOT EXISTS data_deletion_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    confirmation_code VARCHAR(36) UNIQUE NOT NULL,
    meta_user_id VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    scheduled_for TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_deletion_requests_code ON data_deletion_requests(confirmation_code);
CREATE INDEX idx_deletion_requests_status ON data_deletion_requests(status);

ALTER TABLE users ADD COLUMN IF NOT EXISTS meta_user_id VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS scheduled_for_deletion BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deletion_scheduled_at TIMESTAMPTZ;
