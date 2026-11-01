package com.jaswanth.fooddeliveryplatform.delivery.dto;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public record DeliveryPartnerStatusUpdateRequest(
        @NotNull(message = "Approval status is required")
        ApprovalStatus status
) {}
