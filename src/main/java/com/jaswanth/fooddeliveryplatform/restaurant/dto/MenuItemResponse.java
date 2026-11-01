package com.jaswanth.fooddeliveryplatform.restaurant.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MenuItemResponse {

    private Long id;
    private Long restaurantId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isVeg;
    private boolean isAvailable;
    private String imageUrl;
    private LocalDateTime createdAt;

    public MenuItemResponse(Long id, Long restaurantId, Long categoryId, String categoryName,
                            String name, String description, BigDecimal price,
                            boolean isVeg, boolean isAvailable, String imageUrl,
                            LocalDateTime createdAt) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isVeg = isVeg;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getRestaurantId() { return restaurantId; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public boolean isVeg() { return isVeg; }
    public boolean isAvailable() { return isAvailable; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
