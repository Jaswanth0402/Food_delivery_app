package com.jaswanth.fooddeliveryplatform.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(
        @Min(value = 0, message = "Quantity cannot be negative")
        @Max(value = 20, message = "Quantity cannot exceed 20 units")
        int quantity
) {}