package com.jaswanth.fooddeliveryplatform.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long menuItemId,
        String itemName,
        BigDecimal priceAtOrder,
        int quantity,
        BigDecimal subtotal
) {}