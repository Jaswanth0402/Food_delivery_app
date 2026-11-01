package com.jaswanth.fooddeliveryplatform.cart.dto;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long cartItemId;
    private Long menuItemId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;

    public CartItemResponse(Long cartItemId, Long menuItemId, String name,
                            BigDecimal price, int quantity, BigDecimal subtotal) {
        this.cartItemId = cartItemId;
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getCartItemId() { return cartItemId; }
    public Long getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
}
