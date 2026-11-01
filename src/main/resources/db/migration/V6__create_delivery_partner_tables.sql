-- 1. Create delivery_partner_profiles table
CREATE TABLE delivery_partner_profiles (
                                           id BIGINT NOT NULL AUTO_INCREMENT,
                                           user_id BIGINT NOT NULL UNIQUE,
                                           vehicle_number VARCHAR(50) NOT NULL,
                                           license_number VARCHAR(50) NOT NULL,
                                           approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                           is_online BOOLEAN NOT NULL DEFAULT FALSE,
                                           current_latitude DECIMAL(10, 8) NULL,
                                           current_longitude DECIMAL(11, 8) NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                           CONSTRAINT pk_delivery_partner_profiles PRIMARY KEY (id),
                                           CONSTRAINT fk_delivery_partner_user
                                               FOREIGN KEY (user_id)
                                                   REFERENCES users(id)
                                                   ON DELETE CASCADE
);

-- 2. Indexes for fast status lookups and geo-dispatch queries
CREATE INDEX idx_delivery_partner_status ON delivery_partner_profiles(approval_status);
CREATE INDEX idx_delivery_partner_online ON delivery_partner_profiles(is_online);
CREATE INDEX idx_delivery_partner_geo ON delivery_partner_profiles(current_latitude, current_longitude);