CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id UUID         NOT NULL,
    username         VARCHAR(255) NOT NULL,
    CONSTRAINT pk_shopping_carts PRIMARY KEY (shopping_cart_id),
    CONSTRAINT uq_shopping_carts_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS shopping_cart_products (
    shopping_cart_id UUID   NOT NULL,
    product_id       UUID   NOT NULL,
    quantity         BIGINT NOT NULL,
    CONSTRAINT pk_shopping_cart_products PRIMARY KEY (shopping_cart_id, product_id),
    CONSTRAINT fk_shopping_cart_products_cart
        FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts (shopping_cart_id) ON DELETE CASCADE
);
