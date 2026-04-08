CREATE TABLE IF NOT EXISTS orders (
    order_id          UUID           NOT NULL,
    username          VARCHAR(255),
    shopping_cart_id  UUID,
    state             VARCHAR(64)    NOT NULL,
    payment_id        UUID,
    delivery_id       UUID,
    delivery_weight   DOUBLE PRECISION,
    delivery_volume   DOUBLE PRECISION,
    fragile           BOOLEAN,
    total_price       DECIMAL(19, 2),
    delivery_price    DECIMAL(19, 2),
    product_price     DECIMAL(19, 2),
    country           VARCHAR(255),
    city              VARCHAR(255),
    street            VARCHAR(255),
    house             VARCHAR(64),
    flat              VARCHAR(64),
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_orders PRIMARY KEY (order_id)
);

CREATE INDEX IF NOT EXISTS idx_orders_username_created ON orders (username, created_at DESC);

CREATE TABLE IF NOT EXISTS order_line_items (
    order_id   UUID   NOT NULL,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    CONSTRAINT pk_order_line_items PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_line_items_order
        FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);
