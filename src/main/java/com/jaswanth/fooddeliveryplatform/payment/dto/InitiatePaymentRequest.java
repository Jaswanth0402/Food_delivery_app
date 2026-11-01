package com.jaswanth.fooddeliveryplatform.payment.dto;

import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record InitiatePaymentRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}