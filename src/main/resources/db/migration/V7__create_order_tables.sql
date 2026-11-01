-- 1. Orders Master Table
CREATE TABLE orders (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        customer_id BIGINT NOT NULL,
                        restaurant_id BIGINT NOT NULL,
                        delivery_partner_id BIGINT NULL,

    -- Status FSM
                        order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    -- Financial Breakdown (Snapshot at checkout)
                        item_total DECIMAL(10, 2) NOT NULL,
                        tax_amount DECIMAL(10, 2) NOT NULL,
                        delivery_fee DECIMAL(10, 2) NOT NULL,
                        packaging_fee DECIMAL(10, 2) NOT NULL,
                        total_amount DECIMAL(10, 2) NOT NULL,

    -- Delivery Address Snapshot
                        delivery_address VARCHAR(255) NOT NULL,
                        delivery_latitude DECIMAL(10, 8) NULL,
                        delivery_longitude DECIMAL(11, 8) NULL,
                        customer_phone VARCHAR(20) NOT NULL,

                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        CONSTRAINT pk_orders PRIMARY KEY (id),
                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT,
                        CONSTRAINT fk_orders_restaurant
                            FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE RESTRICT,
                        CONSTRAINT fk_orders_delivery_partner
                            FOREIGN KEY (delivery_partner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 2. Order Items Snapshot Table
CREATE TABLE order_items (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             menu_item_id BIGINT NOT NULL,
                             item_name VARCHAR(100) NOT NULL,
                             price_at_order DECIMAL(10, 2) NOT NULL,
                             quantity INT NOT NULL,
                             subtotal DECIMAL(10, 2) NOT NULL,

                             CONSTRAINT pk_order_items PRIMARY KEY (id),
                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_order_items_menu_item
                                 FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT
);

-- Indexes for status lookups, history queries, and driver dispatch
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_restaurant_id ON orders(restaurant_id);
CREATE INDEX idx_orders_delivery_partner_id ON orders(delivery_partner_id);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);