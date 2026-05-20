CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number VARCHAR(30) UNIQUE NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    daily_transfer_limit NUMERIC(10, 2) NOT NULL DEFAULT 1000.00,
    daily_transferred_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    last_transfer_date DATE,
    user_id UUID NOT NULL REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0
);