package com.jaswanth.fooddeliveryplatform.restaurant.controller;

import com.jaswanth.fooddeliveryplatform.restaurant.dto.CategoryRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.CategoryResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.MenuItemRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.MenuItemResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}")
@Tag(name = "Menu Management", description = "Categories, menu items, kitchen stock controls, and customer menu browsing")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // --- Category Endpoints ---

    @Operation(summary = "Add a new menu category (Owner)")
    @PostMapping("/categories")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<CategoryResponse> addCategory(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.addCategory(restaurantId, request, userId));
    }

    @Operation(summary = "Get all categories for a restaurant (Public)")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getCategoriesByRestaurant(restaurantId));
    }

    @Operation(summary = "Delete an empty category (Owner)")
    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        menuService.deleteCategory(restaurantId, categoryId, userId);
        return ResponseEntity.noContent().build();
    }

    // --- Menu Item Endpoints ---

    @Operation(summary = "Add a new menu dish (Owner)")
    @PostMapping("/items")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.addMenuItem(restaurantId, request, userId));
    }

    @Operation(summary = "Browse available dishes for customers (Cached in Redis)")
    @GetMapping("/items")
    public ResponseEntity<List<MenuItemResponse>> getCustomerMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenuItemsForCustomer(restaurantId));
    }

    @Operation(summary = "Get full restaurant menu including out-of-stock items (Owner)")
    @GetMapping("/items/manage")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<MenuItemResponse>> getOwnerMenu(
            @PathVariable Long restaurantId,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(menuService.getMenuItemsForOwner(restaurantId, userId));
    }

    @Operation(summary = "Update an existing dish details (Owner)")
    @PutMapping("/items/{menuItemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            @Valid @RequestBody MenuItemRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(menuService.updateMenuItem(restaurantId, menuItemId, request, userId));
    }

    @Operation(summary = "Kitchen toggle: Set item Available / Out-of-Stock (Owner)")
    @PatchMapping("/items/{menuItemId}/availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            @RequestParam boolean isAvailable,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(menuService.updateMenuItemAvailability(restaurantId, menuItemId, isAvailable, userId));
    }

    @Operation(summary = "Delete dish from menu (Owner)")
    @DeleteMapping("/items/{menuItemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        menuService.deleteMenuItem(restaurantId, menuItemId, userId);
        return ResponseEntity.noContent().build();
    }
}