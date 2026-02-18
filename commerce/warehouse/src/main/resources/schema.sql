CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    width NUMERIC,
    height NUMERIC,
    depth NUMERIC,
    weight NUMERIC,
    fragile BOOLEAN,
    quantity INTEGER
);

CREATE TABLE IF NOT EXISTS order_bookings (
    order_id UUID PRIMARY KEY,
    delivery_id UUID,
    delivery_weight NUMERIC NOT NULL,
    delivery_volume NUMERIC NOT NULL,
    fragile BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS booked_products (
    order_id UUID REFERENCES order_bookings (order_id),
    product_id UUID,
    quantity INTEGER
);