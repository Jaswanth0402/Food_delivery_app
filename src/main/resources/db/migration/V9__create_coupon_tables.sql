CREATE TABLE coupons (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         code VARCHAR(50) NOT NULL UNIQUE,
                         discount_type VARCHAR(20) NOT NULL, -- 'PERCENTAGE' or 'FLAT'
                         discount_value DECIMAL(10, 2) NOT NULL,
                         min_order_value DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
                         max_discount_amount DECIMAL(10, 2) NULL, -- Cap for percentage discounts
                         valid_from TIMESTAMP NOT NULL,
                         valid_until TIMESTAMP NOT NULL,
                         usage_limit INT NULL, -- Total times coupon can be used across platform
                         usage_count INT NOT NULL DEFAULT 0,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT pk_coupons PRIMARY KEY (id)
);

CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_validity ON coupons(is_active, valid_from, valid_until);

-- Add applied coupon snapshot reference to carts and orders
ALTER TABLE carts ADD COLUMN applied_coupon_id BIGINT NULL;
ALTER TABLE carts ADD CONSTRAINT fk_carts_coupon
    FOREIGN KEY (applied_coupon_id) REFERENCES coupons(id) ON DELETE SET NULL;

ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER packaging_fee;
ALTER TABLE orders ADD COLUMN coupon_code VARCHAR(50) NULL AFTER discount_amount;