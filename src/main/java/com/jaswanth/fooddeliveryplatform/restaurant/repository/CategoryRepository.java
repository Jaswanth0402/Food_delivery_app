package com.jaswanth.fooddeliveryplatform.restaurant.repository;

import com.jaswanth.fooddeliveryplatform.restaurant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    // Retrieve all categories under a specific restaurant
    List<Category> findByRestaurantId(Long restaurantId);

    // Find a specific category under a specific restaurant
    Optional<Category> findByIdAndRestaurantId(Long id, Long restaurantId);

    // Check if category name already exists for this restaurant
    boolean existsByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId);
}
