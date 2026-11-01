package com.jaswanth.fooddeliveryplatform.order.entity;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.Restaurant;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id")
    private User deliveryPartner;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "item_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemTotal;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "packaging_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal packagingFee;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Column(name = "delivery_latitude", precision = 10, scale = 8)
    private BigDecimal deliveryLatitude;

    @Column(name = "delivery_longitude", precision = 11, scale = 8)
    private BigDecimal deliveryLongitude;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    public Order() {}

    public Order(User customer, Restaurant restaurant, BigDecimal itemTotal,
                 BigDecimal taxAmount, BigDecimal deliveryFee, BigDecimal packagingFee,
                 BigDecimal totalAmount, String deliveryAddress, BigDecimal deliveryLatitude,
                 BigDecimal deliveryLongitude, String customerPhone) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.itemTotal = itemTotal;
        this.taxAmount = taxAmount;
        this.deliveryFee = deliveryFee;
        this.packagingFee = packagingFee;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.customerPhone = customerPhone;
        this.orderStatus = OrderStatus.PENDING;
    }

    public Long getId() { return id; }
    public User getCustomer() { return customer; }
    public Restaurant getRestaurant() { return restaurant; }
    public User getDeliveryPartner() { return deliveryPartner; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public BigDecimal getItemTotal() { return itemTotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public BigDecimal getPackagingFee() { return packagingFee; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public BigDecimal getDeliveryLatitude() { return deliveryLatitude; }
    public BigDecimal getDeliveryLongitude() { return deliveryLongitude; }
    public String getCustomerPhone() { return customerPhone; }
    public List<OrderItem> getItems() { return items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Update constructor or add setters:
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public String getCouponCode() { return couponCode; }

    public void setDiscountDetails(BigDecimal discountAmount, String couponCode) {
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.couponCode = couponCode;
    }

    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public void setDeliveryPartner(User deliveryPartner) { this.deliveryPartner = deliveryPartner; }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
