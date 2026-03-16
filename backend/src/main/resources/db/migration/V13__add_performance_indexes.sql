-- Índices de performance para consultas frequentes
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_bank_account_id ON transactions(bank_account_id) WHERE bank_account_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_transactions_credit_card_id ON transactions(credit_card_id) WHERE credit_card_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_transactions_user_date ON transactions(user_id, date DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_user_type ON transactions(user_id, type);
CREATE INDEX IF NOT EXISTS idx_transactions_is_recurring ON transactions(is_recurring) WHERE is_recurring = true;

CREATE INDEX IF NOT EXISTS idx_bank_accounts_user_id ON bank_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_savings_goals_user_id ON savings_goals(user_id);
CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories(user_id);
CREATE INDEX IF NOT EXISTS idx_credit_cards_user_id ON credit_cards(user_id);
CREATE INDEX IF NOT EXISTS idx_credit_card_bills_card_month ON credit_card_bills(credit_card_id, year, month);
