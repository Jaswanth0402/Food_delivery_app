package com.jaswanth.fooddeliveryplatform.restaurant.service;

import com.jaswanth.fooddeliveryplatform.restaurant.dto.CategoryRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.CategoryResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.MenuItemRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.MenuItemResponse;

import java.util.List;

public interface MenuService {
    // Category Operations
    CategoryResponse addCategory(Long restaurantId, CategoryRequest request, Long userId);
    List<CategoryResponse> getCategoriesByRestaurant(Long restaurantId);
    void deleteCategory(Long restaurantId, Long categoryId, Long userId);

    // Menu Item Operations
    MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest request, Long userId);
    MenuItemResponse updateMenuItem(Long restaurantId, Long menuItemId, MenuItemRequest request, Long userId);
    List<MenuItemResponse> getMenuItemsForCustomer(Long restaurantId);
    List<MenuItemResponse> getMenuItemsForOwner(Long restaurantId, Long userId);
    MenuItemResponse updateMenuItemAvailability(Long restaurantId, Long menuItemId, boolean isAvailable, Long userId);
    void deleteMenuItem(Long restaurantId, Long menuItemId, Long userId);
}
