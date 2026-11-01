package com.jaswanth.fooddeliveryplatform.user.service;

import com.jaswanth.fooddeliveryplatform.user.dto.AddRoleRequest;
import com.jaswanth.fooddeliveryplatform.user.dto.UpdateProfileRequest;
import com.jaswanth.fooddeliveryplatform.user.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    UserResponse addRole(Long userId, AddRoleRequest request);
}