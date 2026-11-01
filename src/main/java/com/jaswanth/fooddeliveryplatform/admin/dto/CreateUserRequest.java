package com.jaswanth.fooddeliveryplatform.admin.dto;

import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Full name is Required")
        String fullName,

        @NotBlank(message = "Email is Required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is Required")
        @Size(min = 8,message = "Password must contains at lest 8 characters")
        String password,


        @NotNull(message = "Role is Required")
        Role role
) {
}
