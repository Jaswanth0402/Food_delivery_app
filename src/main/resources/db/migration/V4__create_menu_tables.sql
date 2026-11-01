-- 1. Categories Table
CREATE TABLE categories (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            restaurant_id BIGINT NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT pk_categories PRIMARY KEY (id),
                            CONSTRAINT fk_categories_restaurant
                                FOREIGN KEY (restaurant_id)
                                    REFERENCES restaurants(id)
                                    ON DELETE CASCADE
);

-- 2. Menu Items Table
CREATE TABLE menu_items (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            restaurant_id BIGINT NOT NULL,
                            category_id BIGINT NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            price DECIMAL(10, 2) NOT NULL,
                            is_veg BOOLEAN NOT NULL DEFAULT FALSE,
                            is_available BOOLEAN NOT NULL DEFAULT TRUE,
                            image_url VARCHAR(255),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT pk_menu_items PRIMARY KEY (id),
                            CONSTRAINT fk_menu_items_restaurant
                                FOREIGN KEY (restaurant_id)
                                    REFERENCES restaurants(id)
                                    ON DELETE CASCADE,
                            CONSTRAINT fk_menu_items_category
                                FOREIGN KEY (category_id)
                                    REFERENCES categories(id)
                                    ON DELETE RESTRICT
);

-- Indexes for fast query lookup
CREATE INDEX idx_categories_restaurant_id ON categories(restaurant_id);
CREATE INDEX idx_menu_items_restaurant_id ON menu_items(restaurant_id);
CREATE INDEX idx_menu_items_category_id ON menu_items(category_id);