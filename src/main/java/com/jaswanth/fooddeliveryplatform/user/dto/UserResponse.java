package com.jaswanth.fooddeliveryplatform.user.dto;

import com.jaswanth.fooddeliveryplatform.user.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        boolean emailVerified,
        Set<Role> roles,
        LocalDateTime createdAt
) {
}
