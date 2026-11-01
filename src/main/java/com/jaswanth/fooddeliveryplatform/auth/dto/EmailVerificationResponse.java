package com.jaswanth.fooddeliveryplatform.auth.dto;

public record EmailVerificationResponse(
        String status,
        String message
) {}