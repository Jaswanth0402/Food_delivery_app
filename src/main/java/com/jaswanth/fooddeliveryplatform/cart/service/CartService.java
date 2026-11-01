package com.jaswanth.fooddeliveryplatform.cart.service;

import com.jaswanth.fooddeliveryplatform.cart.dto.AddToCartRequest;
import com.jaswanth.fooddeliveryplatform.cart.dto.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addToCart(AddToCartRequest request, Long userId);
    CartResponse updateItemQuantity(Long cartItemId, int quantity, Long userId);
    CartResponse removeItemFromCart(Long cartItemId, Long userId);
    CartResponse applyCoupon(String couponCode, Long userId);
    CartResponse removeCoupon(Long userId);
    void clearCart(Long userId);
}
