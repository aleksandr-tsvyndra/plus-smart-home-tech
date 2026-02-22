CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_payment NUMERIC NOT NULL,
    total_product NUMERIC NOT NULL,
    delivery_total NUMERIC NOT NULL,
    state VARCHAR(10) NOT NULL
);