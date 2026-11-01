-- =========================================================================
-- V11: Add phone and email verification fields to users table
-- =========================================================================

ALTER TABLE users
    ADD COLUMN phone VARCHAR(20) NULL AFTER email,
    ADD COLUMN is_email_verified BOOLEAN NOT NULL DEFAULT FALSE AFTER password,
    ADD COLUMN verification_token VARCHAR(100) NULL AFTER is_email_verified;

-- Add unique constraint on phone (allows multiple NULLs in MySQL, enforces unique active numbers)
ALTER TABLE users
    ADD CONSTRAINT uk_users_phone UNIQUE (phone);

-- Index for instant token lookups during verification link clicks
CREATE INDEX idx_users_verification_token ON users(verification_token);