package com.jaswanth.fooddeliveryplatform.restaurant.dto;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RestaurantResponse {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String address;
    private String phone;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;


    public RestaurantResponse(Long id, Long ownerId, String name, String description,
                              String address, String phone,BigDecimal latitude,
                              BigDecimal longitude, ApprovalStatus approvalStatus,
                              LocalDateTime createdAt){
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.latitude =latitude;
        this.longitude= longitude;
        this.approvalStatus = approvalStatus;
        this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
