CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_account_id UUID NOT NULL REFERENCES accounts(id),
    receiver_account_id UUID NOT NULL REFERENCES accounts(id),
    amount NUMERIC(10, 2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE INDEX idx_transactions_sender ON transactions(sender_account_id);
CREATE INDEX idx_transactions_receiver ON transactions(receiver_account_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);