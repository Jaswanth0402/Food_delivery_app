package com.jaswanth.fooddeliveryplatform.cart.entity;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.MenuItem;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity;

    public CartItem() {}

    public CartItem(Cart cart, MenuItem menuItem, int quantity) {
        this.cart = cart;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
    public Long getId() { return id; }
    public Cart getCart() { return cart; }
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    public void setCart(Cart cart) { this.cart = cart; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
