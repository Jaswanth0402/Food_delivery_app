package com.jaswanth.fooddeliveryplatform.coupon.service;

import com.jaswanth.fooddeliveryplatform.coupon.dto.CouponResponse;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CouponValidationResponse;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CreateCouponRequest;
import com.jaswanth.fooddeliveryplatform.coupon.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CreateCouponRequest request);
    List<CouponResponse> getActiveCoupons();
    Coupon getValidCouponOrThrow(String code, Long userId, BigDecimal currentSubtotal);
    CouponValidationResponse validateAndCalculate(String code, Long userId, BigDecimal currentSubtotal);
    void recordCouponUsage(Long couponId);
}