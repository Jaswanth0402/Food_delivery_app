package com.jaswanth.fooddeliveryplatform.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record GatewayWebhookPayload(
        @NotBlank String event,
        @NotBlank String gatewayOrderId,
        @NotBlank String gatewayPaymentId,
        String failureReason
) {}