package com.jaswanth.fooddeliveryplatform.order.dto;

import com.jaswanth.fooddeliveryplatform.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        String customerName,
        Long restaurantId,
        String restaurantName,
        Long deliveryPartnerId,
        OrderStatus orderStatus,
        BigDecimal itemTotal,
        BigDecimal discountAmount,
        String couponCode,
        BigDecimal taxAmount,
        BigDecimal deliveryFee,
        BigDecimal packagingFee,
        BigDecimal totalAmount,
        String deliveryAddress,
        BigDecimal deliveryLatitude,
        BigDecimal deliveryLongitude,
        String customerPhone,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}