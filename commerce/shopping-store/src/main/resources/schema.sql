CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(50),
    description TEXT,
    image_src VARCHAR,
    quantity_state VARCHAR(20),
    product_state VARCHAR(20),
    product_category VARCHAR(20),
    price DOUBLE
);