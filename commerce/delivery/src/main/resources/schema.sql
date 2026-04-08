CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id     UUID           NOT NULL,
    order_id         UUID           NOT NULL,
    delivery_state   VARCHAR(32)    NOT NULL,
    estimated_cost   DECIMAL(19, 2),
    from_country     VARCHAR(255),
    from_city        VARCHAR(255),
    from_street      VARCHAR(255),
    from_house       VARCHAR(64),
    from_flat        VARCHAR(64),
    to_country       VARCHAR(255),
    to_city          VARCHAR(255),
    to_street        VARCHAR(255),
    to_house         VARCHAR(64),
    to_flat          VARCHAR(64),
    CONSTRAINT pk_deliveries PRIMARY KEY (delivery_id),
    CONSTRAINT uq_deliveries_order_id UNIQUE (order_id)
);
