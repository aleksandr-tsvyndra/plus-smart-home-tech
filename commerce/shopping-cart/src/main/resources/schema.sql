CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(32),
    cart_state VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID REFERENCES shopping_carts (shopping_cart_id),
    product_id UUID,
    quantity INTEGER
);
