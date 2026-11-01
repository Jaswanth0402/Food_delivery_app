package com.jaswanth.fooddeliveryplatform.coupon.dto;

import com.jaswanth.fooddeliveryplatform.coupon.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        BigDecimal maxDiscountAmount,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        int usageCount,
        boolean active
) {}