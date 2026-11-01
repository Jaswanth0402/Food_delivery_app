package com.jaswanth.fooddeliveryplatform.user.repository;

import com.jaswanth.fooddeliveryplatform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Added for phone and email verification workflows
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    Optional<User> findByVerificationToken(String verificationToken);
}
