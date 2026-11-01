package com.jaswanth.fooddeliveryplatform.auth.dto;

public record RegisterResponse(
        Long id,
        String fullName,
        String email
) {
}