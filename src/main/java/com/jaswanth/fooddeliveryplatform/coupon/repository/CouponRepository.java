package com.jaswanth.fooddeliveryplatform.coupon.repository;

import com.jaswanth.fooddeliveryplatform.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeIgnoreCaseAndActiveTrue(String code);

    boolean existsByCodeIgnoreCase(String code);

    List<Coupon> findByActiveTrueAndValidFromBeforeAndValidUntilAfter(
            LocalDateTime fromTime,
            LocalDateTime untilTime
    );
}