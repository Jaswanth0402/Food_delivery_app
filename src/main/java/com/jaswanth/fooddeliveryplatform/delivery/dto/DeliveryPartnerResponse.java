package com.jaswanth.fooddeliveryplatform.delivery.dto;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryPartnerResponse(
        Long id,
        Long userId,
        String vehicleNumber,
        String licenseNumber,
        ApprovalStatus approvalStatus,
        boolean isOnline,
        BigDecimal currentLatitude,
        BigDecimal currentLongitude,
        LocalDateTime createdAt
) {}
