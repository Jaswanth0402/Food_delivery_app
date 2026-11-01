package com.jaswanth.fooddeliveryplatform.coupon.service;

import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CouponResponse;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CouponValidationResponse;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CreateCouponRequest;
import com.jaswanth.fooddeliveryplatform.coupon.entity.Coupon;
import com.jaswanth.fooddeliveryplatform.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest request) {
        String normalizedCode = request.code().trim().toUpperCase();

        if (couponRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new BusinessConflictException("Coupon code '" + normalizedCode + "' already exists.");
        }

        if (request.validUntil().isBefore(request.validFrom())) {
            throw new BusinessConflictException("Coupon validity end time must be after start time.");
        }

        Coupon coupon = new Coupon(
                normalizedCode,
                request.discountType(),
                request.discountValue(),
                request.minOrderValue() != null ? request.minOrderValue() : BigDecimal.ZERO,
                request.maxDiscountAmount(),
                request.validFrom(),
                request.validUntil(),
                request.usageLimit()
        );

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByActiveTrueAndValidFromBeforeAndValidUntilAfter(now, now)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getValidCouponOrThrow(String code, Long userId, BigDecimal currentSubtotal) {
        String normalizedCode = code.trim().toUpperCase();

        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndActiveTrue(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon '" + normalizedCode + "' is invalid or inactive."));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
            throw new BusinessConflictException("Coupon '" + normalizedCode + "' has expired or is not yet active.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new BusinessConflictException("Coupon '" + normalizedCode + "' has reached its maximum global usage limit.");
        }

        if (coupon.getMinOrderValue() != null && currentSubtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new BusinessConflictException(
                    "Order subtotal (₹" + currentSubtotal + ") does not meet the minimum order requirement of ₹" +
                            coupon.getMinOrderValue() + " for coupon " + normalizedCode
            );
        }

        return coupon;
    }

    @Override
    @Transactional(readOnly = true)
    public CouponValidationResponse validateAndCalculate(String code, Long userId, BigDecimal currentSubtotal) {
        Coupon coupon = getValidCouponOrThrow(code, userId, currentSubtotal);
        BigDecimal discount = coupon.calculateDiscount(currentSubtotal);
        return new CouponValidationResponse(
                coupon.getCode(),
                discount,
                "Coupon applied successfully! You saved ₹" + discount
        );
    }

    @Override
    @Transactional
    public void recordCouponUsage(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + couponId));

        coupon.setUsageCount(coupon.getUsageCount() + 1);

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            coupon.setActive(false);
        }

        couponRepository.save(coupon);
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderValue(),
                coupon.getMaxDiscountAmount(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.getUsageCount(),
                coupon.isActive()
        );
    }
}