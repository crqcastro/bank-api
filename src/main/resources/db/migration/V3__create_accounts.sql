CREATE TABLE bankapi.accounts (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID           NOT NULL REFERENCES bankapi.customers(id),
    status      VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    balance     NUMERIC(19, 4) NOT NULL DEFAULT 0.0,
    version     BIGINT         NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_accounts_customer_id ON bankapi.accounts(customer_id);
CREATE INDEX idx_accounts_status      ON bankapi.accounts(status);
