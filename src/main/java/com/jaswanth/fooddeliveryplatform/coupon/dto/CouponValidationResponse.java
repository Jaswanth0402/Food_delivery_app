package com.jaswanth.fooddeliveryplatform.coupon.dto;

import java.math.BigDecimal;

public record CouponValidationResponse(
        String code,
        BigDecimal discountAmount,
        String message
) {}