package com.jaswanth.fooddeliveryplatform.restaurant.dto;

import java.time.LocalDateTime;

public class CategoryResponse {

    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public CategoryResponse(Long id, Long restaurantId, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
