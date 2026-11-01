package com.jaswanth.fooddeliveryplatform.order.dto;

import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CheckoutRequest(
        @NotBlank(message = "Delivery address is required")
        @Size(max = 255, message = "Delivery address must not exceed 255 characters")
        String deliveryAddress,

        BigDecimal deliveryLatitude,
        BigDecimal deliveryLongitude,

        @NotBlank(message = "Contact phone number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Phone number must be a valid 10-digit number")
        String customerPhone,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}