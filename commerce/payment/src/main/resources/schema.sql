CREATE TABLE IF NOT EXISTS payments (
    payment_id     UUID           NOT NULL,
    order_id       UUID           NOT NULL,
    total_payment  DECIMAL(19, 2) NOT NULL,
    delivery_total DECIMAL(19, 2) NOT NULL,
    fee_total      DECIMAL(19, 2) NOT NULL,
    status         VARCHAR(32)    NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (payment_id)
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments (order_id);
