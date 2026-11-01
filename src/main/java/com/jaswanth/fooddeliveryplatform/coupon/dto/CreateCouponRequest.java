package com.jaswanth.fooddeliveryplatform.coupon.dto;

import com.jaswanth.fooddeliveryplatform.coupon.entity.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(
        @NotBlank(message = "Coupon code is required")
        @Size(min = 3, max = 30, message = "Code must be between 3 and 30 characters")
        String code,

        @NotNull(message = "Discount type is required")
        DiscountType discountType,

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0.01", message = "Discount value must be greater than zero")
        BigDecimal discountValue,

        @DecimalMin(value = "0.00", message = "Minimum order value cannot be negative")
        BigDecimal minOrderValue,

        BigDecimal maxDiscountAmount,

        @NotNull(message = "Valid from timestamp is required")
        LocalDateTime validFrom,

        @NotNull(message = "Valid until timestamp is required")
        LocalDateTime validUntil,

        Integer usageLimit
) {}