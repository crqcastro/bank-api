CREATE TABLE bankapi.customers (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255) NOT NULL,
    document      VARCHAR(14)  NOT NULL,
    document_type VARCHAR(10)  NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone         VARCHAR(20),
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_customers_document UNIQUE (document)
);

CREATE INDEX idx_customers_document ON bankapi.customers(document);
CREATE INDEX idx_customers_status   ON bankapi.customers(status);
