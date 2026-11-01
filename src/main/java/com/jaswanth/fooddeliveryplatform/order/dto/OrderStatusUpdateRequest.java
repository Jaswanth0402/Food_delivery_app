package com.jaswanth.fooddeliveryplatform.order.dto;

import com.jaswanth.fooddeliveryplatform.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull(message = "Target order status is required")
        OrderStatus status
) {}