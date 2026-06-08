CREATE TABLE bankapi.transactions (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id  UUID           NOT NULL REFERENCES bankapi.accounts(id),
    type        VARCHAR(20)    NOT NULL,
    amount      NUMERIC(19, 4) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_account_id ON bankapi.transactions(account_id);
CREATE INDEX idx_transactions_created_at ON bankapi.transactions(created_at);
CREATE INDEX idx_transactions_type       ON bankapi.transactions(type);
