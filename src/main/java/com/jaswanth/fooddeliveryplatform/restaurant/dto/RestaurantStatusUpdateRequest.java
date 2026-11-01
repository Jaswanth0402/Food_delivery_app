package com.jaswanth.fooddeliveryplatform.restaurant.dto;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public class RestaurantStatusUpdateRequest {

    @NotNull(message = "Approval status is required")
    private ApprovalStatus status;

    public RestaurantStatusUpdateRequest() {}

    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
}
