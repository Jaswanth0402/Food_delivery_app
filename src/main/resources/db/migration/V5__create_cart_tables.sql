-- 1. Carts Table (One active cart per user)
CREATE TABLE carts (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL UNIQUE,
                       restaurant_id BIGINT,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT pk_carts PRIMARY KEY (id),
                       CONSTRAINT fk_carts_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE,
                       CONSTRAINT fk_carts_restaurant
                           FOREIGN KEY (restaurant_id)
                               REFERENCES restaurants(id)
                               ON DELETE SET NULL
);

-- 2. Cart Items Table
CREATE TABLE cart_items (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            cart_id BIGINT NOT NULL,
                            menu_item_id BIGINT NOT NULL,
                            quantity INT NOT NULL,

                            CONSTRAINT pk_cart_items PRIMARY KEY (id),
                            CONSTRAINT fk_cart_items_cart
                                FOREIGN KEY (cart_id)
                                    REFERENCES carts(id)
                                    ON DELETE CASCADE,
                            CONSTRAINT fk_cart_items_menu_item
                                FOREIGN KEY (menu_item_id)
                                    REFERENCES menu_items(id)
                                    ON DELETE CASCADE,
                            CONSTRAINT uq_cart_item UNIQUE (cart_id, menu_item_id)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);