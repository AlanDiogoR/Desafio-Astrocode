CREATE TABLE credit_card_bills (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  credit_card_id UUID NOT NULL REFERENCES credit_cards(id) ON DELETE CASCADE,
  month INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
  year INTEGER NOT NULL,
  total_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
  status VARCHAR(10) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','CLOSED','PAID')),
  due_date DATE NOT NULL,
  closing_date DATE NOT NULL,
  paid_date DATE,
  paid_from_account_id UUID REFERENCES bank_accounts(id) ON DELETE SET NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (credit_card_id, month, year)
);

CREATE INDEX idx_credit_card_bills_card_id ON credit_card_bills(credit_card_id);
