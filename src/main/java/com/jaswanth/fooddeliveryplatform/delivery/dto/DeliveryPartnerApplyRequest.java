package com.jaswanth.fooddeliveryplatform.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeliveryPartnerApplyRequest(
        @NotBlank(message = "Vehicle number is required")
        @Size(max = 50, message = "Vehicle number must not exceed 50 characters")
        String vehicleNumber,

        @NotBlank(message = "License number is required")
        @Size(max = 50, message = "License number must not exceed 50 characters")
        String licenseNumber
) {

}
