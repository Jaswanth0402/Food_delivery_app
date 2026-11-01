package com.jaswanth.fooddeliveryplatform.restaurant.repository;


import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByOwnerId(Long OwnerId);

    Page<Restaurant> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
    boolean existsByPhone(String phone);
    boolean existsByNameIgnoreCaseAndAddressIgnoreCase(String name, String address);
}
