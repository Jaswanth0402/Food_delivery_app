package com.jaswanth.fooddeliveryplatform.payment.dto;

import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentMethod;
import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String currency,
        String gatewayOrderId
) {}
