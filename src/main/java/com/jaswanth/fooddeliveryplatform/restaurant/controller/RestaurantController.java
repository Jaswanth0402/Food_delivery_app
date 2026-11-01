package com.jaswanth.fooddeliveryplatform.restaurant.controller;

import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantRegistrationRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurants", description = "Restaurant management, public browsing, and onboarding workflows")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(summary = "Register a new restaurant (Customer onboarding)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestaurantResponse> registerRestaurant(
            @Valid @RequestBody RestaurantRegistrationRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        RestaurantResponse response = restaurantService.registerRestaurant(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get restaurants owned by current user")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(Authentication authentication) {
        // authentication.getPrincipal() contains user email or subject
        return ResponseEntity.ok(restaurantService.getMyRestaurants(authentication.getName()));
    }

    @Operation(summary = "Browse approved restaurants (Public)")
    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> getApprovedRestaurants(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(restaurantService.getApprovedRestaurants(pageable));
    }

    @Operation(summary = "Get restaurant details by ID (Public)")
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @Operation(summary = "Admin: Approve or reject restaurant")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> updateApprovalStatus(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantStatusUpdateRequest request) {
        return ResponseEntity.ok(restaurantService.updateApprovalStatus(id, request));
    }
}