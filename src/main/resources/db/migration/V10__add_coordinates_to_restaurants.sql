-- Alter restaurants to add high-precision GPS coordinates
ALTER TABLE restaurants
    ADD COLUMN latitude DECIMAL(10, 8) NULL AFTER phone,
    ADD COLUMN longitude DECIMAL(11, 8) NULL AFTER latitude,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- Spatial lookup and query index
CREATE INDEX idx_restaurants_geo ON restaurants(latitude, longitude);