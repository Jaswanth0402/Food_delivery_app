package com.jaswanth.fooddeliveryplatform.restaurant.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Category() {}

    public Category(Restaurant restaurant, String name, String description) {
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;

    }

    public Long getId() { return id; }
    public Restaurant getRestaurant() { return restaurant; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }


}
