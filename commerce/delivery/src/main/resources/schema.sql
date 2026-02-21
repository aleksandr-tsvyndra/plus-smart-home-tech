CREATE TABLE IF NOT EXISTS addresses (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100) NOT NULL,
    house VARCHAR(10) NOT NULL,
    flat VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    from_address_id UUID REFERENCES addresses (address_id) NOT NULL,
    to_address_id UUID REFERENCES addresses (address_id) NOT NULL,
    state VARCHAR(20) NOT NULL
);