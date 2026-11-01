package com.jaswanth.fooddeliveryplatform.order.entity;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.MenuItem;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "price_at_order", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtOrder;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public OrderItem() {}

    public OrderItem(Order order, MenuItem menuItem, String itemName,
                     BigDecimal priceAtOrder, int quantity, BigDecimal subtotal) {
        this.order = order;
        this.menuItem = menuItem;
        this.itemName = itemName;
        this.priceAtOrder = priceAtOrder;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public MenuItem getMenuItem() { return menuItem; }
    public String getItemName() { return itemName; }
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }

    public void setOrder(Order order) { this.order = order; }
}
