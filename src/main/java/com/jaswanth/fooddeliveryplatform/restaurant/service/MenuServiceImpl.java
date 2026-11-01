package com.jaswanth.fooddeliveryplatform.restaurant.service;

import com.jaswanth.fooddeliveryplatform.common.config.RedisConfig;
import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.CategoryRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.CategoryResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.MenuItemRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.MenuItemResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.Category;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.MenuItem;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.Restaurant;
import com.jaswanth.fooddeliveryplatform.restaurant.repository.CategoryRepository;
import com.jaswanth.fooddeliveryplatform.restaurant.repository.MenuItemRepository;
import com.jaswanth.fooddeliveryplatform.restaurant.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuServiceImpl(RestaurantRepository restaurantRepository,
                           CategoryRepository categoryRepository,
                           MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    // =========================================================================
    // Category Operations
    // =========================================================================

    @Override
    @Transactional
    @CacheEvict(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public CategoryResponse addCategory(Long restaurantId, CategoryRequest request, Long userId) {
        Restaurant restaurant = getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);

        String name = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndRestaurantId(name, restaurantId)) {
            throw new BusinessConflictException("Category with name '" + name + "' already exists for this restaurant");
        }

        Category category = new Category(
                restaurant,
                name,
                request.getDescription() != null ? request.getDescription().trim() : null
        );
        Category saved = categoryRepository.save(category);
        return mapToCategoryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return categoryRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public void deleteCategory(Long restaurantId, Long categoryId, Long userId) {
        getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);

        Category category = categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (menuItemRepository.existsByCategoryId(categoryId)) {
            throw new BusinessConflictException("Cannot delete category because it contains active menu items. Reassign or delete the dishes first.");
        }

        categoryRepository.delete(category);
    }

    // =========================================================================
    // MenuItem Operations
    // =========================================================================

    @Override
    @Transactional
    @CacheEvict(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest request, Long userId) {
        Restaurant restaurant = getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);

        Category category = categoryRepository.findByIdAndRestaurantId(request.getCategoryId(), restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not belong to this restaurant"));

        String itemName = request.getName().trim();
        if (menuItemRepository.existsByNameIgnoreCaseAndRestaurantId(itemName, restaurantId)) {
            throw new BusinessConflictException("A dish named '" + itemName + "' already exists in this restaurant menu");
        }

        MenuItem menuItem = new MenuItem(
                restaurant,
                category,
                itemName,
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getPrice(),
                request.isVeg(),
                request.isAvailable(),
                request.getImageUrl() != null ? request.getImageUrl().trim() : null
        );

        MenuItem saved = menuItemRepository.save(menuItem);
        return mapToMenuItemResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public MenuItemResponse updateMenuItem(Long restaurantId, Long menuItemId, MenuItemRequest request, Long userId) {
        Restaurant restaurant = getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);

        MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found in this restaurant with id: " + menuItemId));

        Category category = categoryRepository.findByIdAndRestaurantId(request.getCategoryId(), restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not belong to this restaurant"));

        String updatedName = request.getName().trim();
        if (!menuItem.getName().equalsIgnoreCase(updatedName) &&
                menuItemRepository.existsByNameIgnoreCaseAndRestaurantId(updatedName, restaurantId)) {
            throw new BusinessConflictException("Another dish with name '" + updatedName + "' already exists in this restaurant");
        }

        menuItem.setCategory(category);
        menuItem.setName(updatedName);
        menuItem.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        menuItem.setPrice(request.getPrice());
        menuItem.setVeg(request.isVeg());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);

        MenuItem updated = menuItemRepository.save(menuItem);
        return mapToMenuItemResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public List<MenuItemResponse> getMenuItemsForCustomer(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId)
                .stream()
                .map(this::mapToMenuItemResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsForOwner(Long restaurantId, Long userId) {
        getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToMenuItemResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public MenuItemResponse updateMenuItemAvailability(Long restaurantId, Long menuItemId, boolean isAvailable, Long userId) {
        getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);

        MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found in this restaurant with id: " + menuItemId));

        menuItem.setAvailable(isAvailable);
        MenuItem updated = menuItemRepository.save(menuItem);
        return mapToMenuItemResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public void deleteMenuItem(Long restaurantId, Long menuItemId, Long userId) {
        getApprovedRestaurantAndVerifyOwnership(restaurantId, userId);

        MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found in this restaurant with id: " + menuItemId));

        menuItemRepository.delete(menuItem);
    }

    // =========================================================================
    // Helper Methods
    // =========================================================================

    private Restaurant getApprovedRestaurantAndVerifyOwnership(Long restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        Long ownerId = (restaurant.getOwner() != null) ? restaurant.getOwner().getId() : restaurant.getOwnerId();
        if (!ownerId.equals(userId)) {
            throw new AccessDeniedException("You do not have permission to manage this restaurant's menu");
        }

        if (restaurant.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BusinessConflictException("Restaurant is not approved yet (Status: " +
                    restaurant.getApprovalStatus() + "). Menu editing is restricted until approved.");
        }

        return restaurant;
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getRestaurant().getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }

    private MenuItemResponse mapToMenuItemResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurant().getId(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.isVeg(),
                item.isAvailable(),
                item.getImageUrl(),
                item.getCreatedAt()
        );
    }
}