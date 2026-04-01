CREATE TABLE IF NOT EXISTS products (
    product_id        UUID          NOT NULL,
    product_name      VARCHAR(255)  NOT NULL,
    description       VARCHAR(4096) NOT NULL,
    image_src         VARCHAR(2048),
    quantity_state    VARCHAR(32)   NOT NULL,
    product_state     VARCHAR(32)   NOT NULL,
    product_category  VARCHAR(32),
    price             DECIMAL(19, 2) NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (product_id)
);
