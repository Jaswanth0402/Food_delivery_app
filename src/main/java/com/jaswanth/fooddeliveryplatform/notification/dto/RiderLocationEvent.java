package com.jaswanth.fooddeliveryplatform.notification.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RiderLocationEvent(
        Long orderId,
        Long deliveryPartnerId,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime timestamp
) {}