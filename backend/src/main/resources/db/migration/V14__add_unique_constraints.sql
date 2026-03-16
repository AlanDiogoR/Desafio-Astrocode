-- Previne nomes duplicados via DB
ALTER TABLE bank_accounts ADD CONSTRAINT uq_bank_accounts_user_name UNIQUE (user_id, name);
ALTER TABLE categories ADD CONSTRAINT uq_categories_user_name_type UNIQUE (user_id, name, type);
ALTER TABLE credit_cards ADD CONSTRAINT uq_credit_cards_user_name UNIQUE (user_id, name);

-- Previne transação órfã (sem bank_account nem credit_card)
ALTER TABLE transactions ADD CONSTRAINT chk_transaction_account_or_card
    CHECK (bank_account_id IS NOT NULL OR credit_card_id IS NOT NULL);
