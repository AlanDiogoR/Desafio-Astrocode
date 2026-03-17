CREATE TABLE IF NOT EXISTS pluggy_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  pluggy_item_id VARCHAR(255) NOT NULL UNIQUE,
  institution_name VARCHAR(150),
  status VARCHAR(50),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_pluggy_items_user_id ON pluggy_items(user_id);
CREATE INDEX idx_pluggy_items_pluggy_item_id ON pluggy_items(pluggy_item_id);

ALTER TABLE bank_accounts ADD COLUMN IF NOT EXISTS pluggy_account_id VARCHAR(255);
ALTER TABLE bank_accounts ADD COLUMN IF NOT EXISTS pluggy_item_id VARCHAR(255);
CREATE INDEX IF NOT EXISTS idx_bank_accounts_pluggy_account_id ON bank_accounts(pluggy_account_id) WHERE pluggy_account_id IS NOT NULL;
