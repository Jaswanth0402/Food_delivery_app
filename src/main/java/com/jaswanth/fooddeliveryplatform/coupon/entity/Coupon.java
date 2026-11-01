package com.jaswanth.fooddeliveryplatform.coupon.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count", nullable = false)
    private int usageCount = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Coupon() {}

    public Coupon(String code, DiscountType discountType, BigDecimal discountValue,
                  BigDecimal minOrderValue, BigDecimal maxDiscountAmount,
                  LocalDateTime validFrom, LocalDateTime validUntil, Integer usageLimit) {
        this.code = code.toUpperCase().trim();
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue != null ? minOrderValue : BigDecimal.ZERO;
        this.maxDiscountAmount = maxDiscountAmount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.usageLimit = usageLimit;
        this.active = true;
    }

    public boolean isValid(BigDecimal orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        if (!active) return false;
        if (now.isBefore(validFrom) || now.isAfter(validUntil)) return false;
        if (orderAmount.compareTo(minOrderValue) < 0) return false;
        if (usageLimit != null && usageCount >= usageLimit) return false;
        return true;
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isValid(orderAmount)) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (discountType == DiscountType.FLAT) {
            discount = discountValue;
        } else {
            // PERCENTAGE: (orderAmount * discountValue) / 100
            discount = orderAmount.multiply(discountValue)
                    .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
            if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                discount = maxDiscountAmount;
            }
        }

        // Discount cannot exceed the subtotal
        return discount.min(orderAmount);
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public DiscountType getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public LocalDateTime getValidUntil() { return validUntil; }
    public Integer getUsageLimit() { return usageLimit; }
    public int getUsageCount() { return usageCount; }
    public boolean isActive() { return active; }
    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public void setActive(boolean active) { this.active = active; }
}
