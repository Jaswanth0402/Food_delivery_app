package com.jaswanth.fooddeliveryplatform.notification.dto;

import com.jaswanth.fooddeliveryplatform.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderStatusEvent(
        Long orderId,
        OrderStatus status,
        String message,
        LocalDateTime timestamp
) {}