CREATE TABLE payments (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          order_id BIGINT NOT NULL UNIQUE,
                          customer_id BIGINT NOT NULL,
                          payment_method VARCHAR(30) NOT NULL,
                          payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                          amount DECIMAL(10, 2) NOT NULL,
                          currency VARCHAR(10) NOT NULL DEFAULT 'INR',
                          gateway_order_id VARCHAR(100) NULL,
                          gateway_payment_id VARCHAR(100) NULL,
                          failure_reason VARCHAR(255) NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT pk_payments PRIMARY KEY (id),
                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                          CONSTRAINT fk_payments_customer
                              FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_payments_gateway_order_id ON payments(gateway_order_id);
CREATE INDEX idx_payments_status ON payments(payment_status);