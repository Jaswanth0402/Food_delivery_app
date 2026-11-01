package com.jaswanth.fooddeliveryplatform.user.controller;

import com.jaswanth.fooddeliveryplatform.user.dto.AddRoleRequest;
import com.jaswanth.fooddeliveryplatform.user.dto.UpdateProfileRequest;
import com.jaswanth.fooddeliveryplatform.user.dto.UserResponse;
import com.jaswanth.fooddeliveryplatform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userService.getCurrentUser(userId);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

//    @PostMapping("/me/roles")
//    public UserResponse addRole(
//            Authentication authentication,
//            @Valid @RequestBody AddRoleRequest request
//    ) {
//        Long userId = Long.valueOf(authentication.getName());
//        return userService.addRole(userId, request);
//    }

    @PostMapping("/me/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(
            Authentication authentication,
            @Valid @RequestBody AddRoleRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.addRole(userId, request));
    }
}
