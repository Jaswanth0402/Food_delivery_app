package com.jaswanth.fooddeliveryplatform.restaurant.repository;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // For Restaurant Owners: view all dishes including out-of-stock items
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // For Customers: view only available dishes of an approved restaurant
    List<MenuItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);

    // Filter items by category
    List<MenuItem> findByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);

    // Find a specific menu item under a specific restaurant
    Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);

    // Check if category has linked items before deletion
    boolean existsByCategoryId(Long categoryId);
    boolean existsByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId);
}
