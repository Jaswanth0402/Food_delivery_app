package com.jaswanth.fooddeliveryplatform.admin.dto;

import com.jaswanth.fooddeliveryplatform.user.entity.Role;

import java.util.Set;

public record CreateUserResponse(
        Long id,
        String fullName,
        String email,
        Set<Role> role
) {
}
