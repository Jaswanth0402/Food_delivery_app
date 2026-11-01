package com.jaswanth.fooddeliveryplatform.user.dto;
import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import jakarta.validation.constraints.NotNull;

public record AddRoleRequest(
        @NotNull(message = "Role is required")
        Role role
) {
}
