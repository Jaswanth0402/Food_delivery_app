package com.jaswanth.fooddeliveryplatform.restaurant.service;


import com.jaswanth.fooddeliveryplatform.common.exception.auth.EmailNotVerifiedException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.util.PhoneNumberFormatter;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantRegistrationRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantResponse;
import com.jaswanth.fooddeliveryplatform.restaurant.dto.RestaurantStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.Restaurant;
import com.jaswanth.fooddeliveryplatform.restaurant.repository.RestaurantRepository;
import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RestaurantServiceImpl implements RestaurantService{
    private final RestaurantRepository restaurantRepository;
    private  final UserRepository userRepository;
    private final PhoneNumberFormatter phoneNumberFormatter;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 UserRepository userRepository,
                                 PhoneNumberFormatter phoneNumberFormatter) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.phoneNumberFormatter = phoneNumberFormatter;
    }

    @Override
    @Transactional
    public RestaurantResponse registerRestaurant(RestaurantRegistrationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // 1. Enforce verified email constraint prior to merchant onboarding
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("You must verify your email address before registering a restaurant.");
        }

        // 2. Validate and normalize restaurant phone to E.164
        String formattedPhone = phoneNumberFormatter.formatToE164(request.getPhone());

        if (restaurantRepository.existsByPhone(formattedPhone)) {
            throw new BusinessConflictException("A restaurant with contact phone '" + formattedPhone + "' already exists.");
        }

        // 3. Prevent duplicate restaurant at same address
        if (restaurantRepository.existsByNameIgnoreCaseAndAddressIgnoreCase(request.getName().trim(), request.getAddress().trim())) {
            throw new BusinessConflictException("A restaurant named '" + request.getName().trim() + "' is already registered at this address.");
        }

        Restaurant restaurant = new Restaurant(
                user,
                request.getName().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getAddress().trim(),
                formattedPhone,
                request.getLatitude(),
                request.getLongitude(),
                ApprovalStatus.PENDING
        );

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return mapToResponse(savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getMyRestaurants(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        return restaurantRepository.findByOwnerId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return mapToResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getApprovedRestaurants(Pageable pageable) {
        return restaurantRepository.findByApprovalStatus(ApprovalStatus.APPROVED, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public RestaurantResponse updateApprovalStatus(Long id, RestaurantStatusUpdateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        ApprovalStatus newStatus = request.getStatus();

        if (restaurant.getApprovalStatus() == newStatus) {
            throw new BusinessConflictException("Restaurant is already in " + newStatus + " status.");
        }

        restaurant.setApprovalStatus(newStatus);

        // Swiggy/Zomato Flow: Grant RESTAURANT_OWNER role upon approval
        if (newStatus == ApprovalStatus.APPROVED) {
            User owner = restaurant.getOwner();
            if (!owner.getRole().contains(Role.RESTAURANT_OWNER)) {
                owner.addRole(Role.RESTAURANT_OWNER);
                userRepository.save(owner);
            }
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return mapToResponse(updatedRestaurant);
    }

    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getOwnerId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getApprovalStatus(),
                restaurant.getCreatedAt()
        );
    }


}
