CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID PRIMARY KEY,
    width NUMERIC,
    height NUMERIC,
    depth NUMERIC,
    weight NUMERIC,
    fragile BOOLEAN,
    quantity INTEGER
);