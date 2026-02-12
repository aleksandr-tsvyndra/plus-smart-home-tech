CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY DEFAULT get_random_uuid(),
    state VARCHAR(20) NOT NULL,
    shopping_cart_id UUID NOT NULL,
    delivery_id UUID,
    payment_id UUID,
    delivery_volume NUMERIC,
    delivery_weight NUMERIC,
    fragile BOOLEAN,
    total_price NUMERIC,
    product_price NUMERIC,
    delivery_price NUMERIC,
    username VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID REFERENCES orders (order_id),
    product_id UUID,
    quantity INTEGER
);