CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(500),
    description VARCHAR(1000),
    image_src VARCHAR(2500),
    quantity_state VARCHAR(10),
    product_state VARCHAR(10),
    product_category VARCHAR(10),
    price NUMERIC
);