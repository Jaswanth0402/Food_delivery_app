package com.jaswanth.fooddeliveryplatform.delivery.service;

import com.jaswanth.fooddeliveryplatform.common.exception.auth.EmailNotVerifiedException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerApplyRequest;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerResponse;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.delivery.entity.DeliveryPartnerProfile;
import com.jaswanth.fooddeliveryplatform.delivery.repository.DeliveryPartnerProfileRepository;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final DriverLocationService driverLocationService;

    public DeliveryPartnerServiceImpl(DeliveryPartnerProfileRepository profileRepository,
                                      UserRepository userRepository,
                                      DriverLocationService driverLocationService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.driverLocationService = driverLocationService;
    }

    @Override
    @Transactional
    public DeliveryPartnerResponse apply(DeliveryPartnerApplyRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // 1. Enforce verified email constraint prior to onboarding
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("You must verify your email address before applying as a delivery partner.");
        }

        // 2. One application per user profile
        if (profileRepository.existsByUserId(userId)) {
            throw new BusinessConflictException("You have already submitted a delivery partner application.");
        }

        String vehicleNo = request.vehicleNumber().trim().toUpperCase();
        String licenseNo = request.licenseNumber().trim().toUpperCase();

        // 3. Unique Vehicle and Driving License validation
        if (profileRepository.existsByVehicleNumberIgnoreCase(vehicleNo)) {
            throw new BusinessConflictException("Vehicle number '" + vehicleNo + "' is already registered with another partner.");
        }

        if (profileRepository.existsByLicenseNumberIgnoreCase(licenseNo)) {
            throw new BusinessConflictException("Driving license number '" + licenseNo + "' is already registered on the platform.");
        }

        DeliveryPartnerProfile profile = new DeliveryPartnerProfile(
                user,
                vehicleNo,
                licenseNo
        );

        DeliveryPartnerProfile saved = profileRepository.save(profile);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryPartnerResponse getMyProfile(Long userId) {
        DeliveryPartnerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner profile not found for user: " + userId));
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public DeliveryPartnerResponse toggleOnlineStatus(Long userId, boolean isOnline) {
        DeliveryPartnerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner profile not found for user: " + userId));

        if (profile.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BusinessConflictException("Your profile must be approved before you can go online (Current status: " + profile.getApprovalStatus() + ").");
        }

        profile.setOnline(isOnline);
        DeliveryPartnerProfile saved = profileRepository.save(profile);

        // Redis Geospatial synchronization
        if (isOnline && profile.getCurrentLatitude() != null && profile.getCurrentLongitude() != null) {
            driverLocationService.updateDriverLocation(userId, profile.getCurrentLatitude(), profile.getCurrentLongitude());
        } else if (!isOnline) {
            driverLocationService.removeDriverLocation(userId);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DeliveryPartnerResponse updateLocation(Long userId, BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null ||
                latitude.doubleValue() < -90.0 || latitude.doubleValue() > 90.0 ||
                longitude.doubleValue() < -180.0 || longitude.doubleValue() > 180.0) {
            throw new BusinessConflictException("Invalid geographical coordinates: Latitude must be between -90 and 90, Longitude between -180 and 180.");
        }

        DeliveryPartnerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner profile not found for user: " + userId));

        profile.setCurrentLatitude(latitude);
        profile.setCurrentLongitude(longitude);
        DeliveryPartnerProfile saved = profileRepository.save(profile);

        // Update high-throughput Redis Geo index if the partner is currently online
        if (profile.isOnline()) {
            driverLocationService.updateDriverLocation(userId, latitude, longitude);
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryPartnerResponse> getPendingApplications(Pageable pageable) {
        return profileRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public DeliveryPartnerResponse updateApprovalStatus(Long profileId, DeliveryPartnerStatusUpdateRequest request) {
        DeliveryPartnerProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner profile not found with id: " + profileId));

        ApprovalStatus newStatus = request.status();
        if (profile.getApprovalStatus() == newStatus) {
            throw new BusinessConflictException("Delivery partner profile is already in " + newStatus + " status.");
        }

        profile.setApprovalStatus(newStatus);

        // Swiggy/Zomato Auto-Elevation: Automatically grant DELIVERY_PARTNER role upon approval
        if (newStatus == ApprovalStatus.APPROVED) {
            User user = profile.getUser();
            if (!user.getRole().contains(Role.DELIVERY_PARTNER)) {
                user.addRole(Role.DELIVERY_PARTNER);
                userRepository.save(user);
            }
        } else if (newStatus == ApprovalStatus.REJECTED) {
            // Take driver offline and remove from Redis Geo index if rejected
            profile.setOnline(false);
            driverLocationService.removeDriverLocation(profile.getUser().getId());
        }

        DeliveryPartnerProfile updated = profileRepository.save(profile);
        return mapToResponse(updated);
    }

    private DeliveryPartnerResponse mapToResponse(DeliveryPartnerProfile profile) {
        return new DeliveryPartnerResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getVehicleNumber(),
                profile.getLicenseNumber(),
                profile.getApprovalStatus(),
                profile.isOnline(),
                profile.getCurrentLatitude(),
                profile.getCurrentLongitude(),
                profile.getCreatedAt()
        );
    }
}