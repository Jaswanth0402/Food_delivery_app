package com.jaswanth.fooddeliveryplatform.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private Long cartId;
    private Long restaurantId;
    private String restaurantName;
    private List<CartItemResponse> items;
    private BigDecimal itemTotal;
    private BigDecimal discountAmount;
    private String appliedCouponCode;
    private BigDecimal deliveryFee;
    private BigDecimal packagingFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    public CartResponse(Long cartId, Long restaurantId, String restaurantName,
                        List<CartItemResponse> items, BigDecimal itemTotal,
                        BigDecimal discountAmount, String appliedCouponCode,
                        BigDecimal deliveryFee, BigDecimal packagingFee,
                        BigDecimal taxAmount, BigDecimal totalAmount) {
        this.cartId = cartId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = items;
        this.itemTotal = itemTotal;
        this.discountAmount = discountAmount;
        this.appliedCouponCode = appliedCouponCode;
        this.deliveryFee = deliveryFee;
        this.packagingFee = packagingFee;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }

    public Long getCartId() { return cartId; }
    public Long getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public List<CartItemResponse> getItems() { return items; }
    public BigDecimal getItemTotal() { return itemTotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public String getAppliedCouponCode() { return appliedCouponCode; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public BigDecimal getPackagingFee() { return packagingFee; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
