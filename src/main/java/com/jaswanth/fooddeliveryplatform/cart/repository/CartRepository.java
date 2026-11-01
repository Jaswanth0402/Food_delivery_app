package com.jaswanth.fooddeliveryplatform.cart.repository;

import com.jaswanth.fooddeliveryplatform.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Retrieve active cart by owner's user ID
    Optional<Cart> findByUserId(Long userId);
}
