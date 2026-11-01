package com.jaswanth.fooddeliveryplatform.cart.repository;

import com.jaswanth.fooddeliveryplatform.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Check if a specific menu item is already present in this cart
    Optional<CartItem> findByCartIdAndMenuItemId(Long cartId, Long menuItemId);

    // Delete a specific item belonging to a specific cart
    void deleteByIdAndCartId(Long id, Long cartId);

}
