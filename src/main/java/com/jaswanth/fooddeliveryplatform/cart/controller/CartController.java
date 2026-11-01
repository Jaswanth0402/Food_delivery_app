package com.jaswanth.fooddeliveryplatform.cart.controller;

import com.jaswanth.fooddeliveryplatform.cart.dto.AddToCartRequest;
import com.jaswanth.fooddeliveryplatform.cart.dto.CartResponse;
import com.jaswanth.fooddeliveryplatform.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    // Add item to cart or increment quantity
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(cartService.addToCart(request, userId));
    }

    // Update item quantity directly (e.g. set to 3, or set to 0 to remove)
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int quantity,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(cartService.updateItemQuantity(cartItemId, quantity, userId));
    }

    // Remove single item from cart
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(cartService.removeItemFromCart(cartItemId, userId));
    }

    // Clear entire cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }


}
