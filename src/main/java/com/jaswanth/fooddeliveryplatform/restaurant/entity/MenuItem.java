package com.jaswanth.fooddeliveryplatform.restaurant.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_veg", nullable = false)
    private boolean veg;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public MenuItem() {}

    public MenuItem(Restaurant restaurant, Category category, String name,
                    String description, BigDecimal price, boolean veg,
                    boolean available, String imageUrl) {
        this.restaurant = restaurant;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.veg = veg;
        this.available = available;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public Restaurant getRestaurant() { return restaurant; }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public boolean isVeg() { return veg; }
    public boolean isAvailable() { return available; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    public void setCategory(Category category) { this.category = category; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setVeg(boolean veg) { this.veg = veg; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }


}
