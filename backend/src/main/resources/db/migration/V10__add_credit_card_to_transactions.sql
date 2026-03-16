ALTER TABLE transactions
  ADD COLUMN credit_card_id UUID REFERENCES credit_cards(id) ON DELETE SET NULL,
  ADD COLUMN credit_card_bill_id UUID REFERENCES credit_card_bills(id) ON DELETE SET NULL;

ALTER TABLE transactions ALTER COLUMN bank_account_id DROP NOT NULL;
