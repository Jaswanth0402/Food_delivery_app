-- =========================================================================
-- V12: Add signature column and unique index for idempotency
-- =========================================================================

ALTER TABLE payments
    ADD COLUMN gateway_signature VARCHAR(255) NULL AFTER gateway_payment_id;

CREATE UNIQUE INDEX uk_payments_gateway_payment_id
    ON payments(gateway_payment_id);