-- ============================================================
-- Deduplicação de bank_accounts
-- Mantém a conta com mais transações (ou a com menor UUID como desempate)
-- Redireciona transações das duplicatas para a conta mantida
-- ============================================================
DO $$
DECLARE
    dup RECORD;
    keep_id UUID;
BEGIN
    FOR dup IN (
        SELECT user_id, name
        FROM bank_accounts
        GROUP BY user_id, name
        HAVING COUNT(*) > 1
    ) LOOP
        SELECT ba.id INTO keep_id
        FROM bank_accounts ba
        LEFT JOIN transactions t ON t.bank_account_id = ba.id
        WHERE ba.user_id = dup.user_id AND ba.name = dup.name
        GROUP BY ba.id
        ORDER BY COUNT(t.id) DESC, ba.id ASC
        LIMIT 1;

        UPDATE transactions
        SET bank_account_id = keep_id
        WHERE bank_account_id IN (
            SELECT id FROM bank_accounts
            WHERE user_id = dup.user_id AND name = dup.name AND id <> keep_id
        );

        DELETE FROM bank_accounts
        WHERE user_id = dup.user_id AND name = dup.name AND id <> keep_id;
    END LOOP;
END$$;

-- ============================================================
-- Deduplicação de categories
-- ============================================================
DO $$
DECLARE
    dup RECORD;
    keep_id UUID;
BEGIN
    FOR dup IN (
        SELECT user_id, name, type
        FROM categories
        GROUP BY user_id, name, type
        HAVING COUNT(*) > 1
    ) LOOP
        SELECT c.id INTO keep_id
        FROM categories c
        LEFT JOIN transactions t ON t.category_id = c.id
        WHERE c.user_id = dup.user_id AND c.name = dup.name AND c.type = dup.type
        GROUP BY c.id
        ORDER BY COUNT(t.id) DESC, c.id ASC
        LIMIT 1;

        UPDATE transactions
        SET category_id = keep_id
        WHERE category_id IN (
            SELECT id FROM categories
            WHERE user_id = dup.user_id AND name = dup.name AND type = dup.type AND id <> keep_id
        );

        DELETE FROM categories
        WHERE user_id = dup.user_id AND name = dup.name AND type = dup.type AND id <> keep_id;
    END LOOP;
END$$;

-- ============================================================
-- Deduplicação de credit_cards
-- Redireciona transações e bills antes de deletar duplicatas
-- ============================================================
DO $$
DECLARE
    dup RECORD;
    keep_id UUID;
BEGIN
    FOR dup IN (
        SELECT user_id, name
        FROM credit_cards
        GROUP BY user_id, name
        HAVING COUNT(*) > 1
    ) LOOP
        SELECT cc.id INTO keep_id
        FROM credit_cards cc
        LEFT JOIN transactions t ON t.credit_card_id = cc.id
        WHERE cc.user_id = dup.user_id AND cc.name = dup.name
        GROUP BY cc.id
        ORDER BY COUNT(t.id) DESC, cc.id ASC
        LIMIT 1;

        UPDATE transactions
        SET credit_card_id = keep_id
        WHERE credit_card_id IN (
            SELECT id FROM credit_cards
            WHERE user_id = dup.user_id AND name = dup.name AND id <> keep_id
        );

        UPDATE credit_card_bills
        SET credit_card_id = keep_id
        WHERE credit_card_id IN (
            SELECT id FROM credit_cards
            WHERE user_id = dup.user_id AND name = dup.name AND id <> keep_id
        );

        DELETE FROM credit_cards
        WHERE user_id = dup.user_id AND name = dup.name AND id <> keep_id;
    END LOOP;
END$$;

-- ============================================================
-- Adiciona constraints únicas após a deduplicação
-- ============================================================
ALTER TABLE bank_accounts ADD CONSTRAINT uq_bank_accounts_user_name UNIQUE (user_id, name);
ALTER TABLE categories ADD CONSTRAINT uq_categories_user_name_type UNIQUE (user_id, name, type);
ALTER TABLE credit_cards ADD CONSTRAINT uq_credit_cards_user_name UNIQUE (user_id, name);

-- Previne transação órfã (sem bank_account nem credit_card)
ALTER TABLE transactions ADD CONSTRAINT chk_transaction_account_or_card
    CHECK (bank_account_id IS NOT NULL OR credit_card_id IS NOT NULL);
