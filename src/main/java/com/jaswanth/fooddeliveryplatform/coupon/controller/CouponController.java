package com.jaswanth.fooddeliveryplatform.coupon.controller;

import com.jaswanth.fooddeliveryplatform.coupon.dto.CouponResponse;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CouponValidationResponse;
import com.jaswanth.fooddeliveryplatform.coupon.dto.CreateCouponRequest;
import com.jaswanth.fooddeliveryplatform.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupons", description = "Coupon creation, promotional listing, and discount validation")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Admin: Create a new promotional coupon")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(request));
    }

    @Operation(summary = "Get all active coupons (Public / Customer)")
    @GetMapping("/active")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }

    @Operation(summary = "Test and calculate discount for a subtotal (Customer)")
    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CouponValidationResponse> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal subtotal,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(couponService.validateAndCalculate(code, userId, subtotal));
    }
}