package com.jaswanth.fooddeliveryplatform.restaurant.service;

import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantRegistrationRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse registerRestaurant(RestaurantRegistrationRequest request, Long userId);

    List<RestaurantResponse> getMyRestaurants(String userEmail);

    RestaurantResponse getRestaurantById(Long id);

    Page<RestaurantResponse> getApprovedRestaurants(Pageable pageable);

    RestaurantResponse updateApprovalStatus(Long id, RestaurantStatusUpdateRequest request);


}
