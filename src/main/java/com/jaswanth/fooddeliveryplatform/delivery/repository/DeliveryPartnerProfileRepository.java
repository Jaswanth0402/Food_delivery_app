package com.jaswanth.fooddeliveryplatform.delivery.repository;

import com.jaswanth.fooddeliveryplatform.delivery.entity.DeliveryPartnerProfile;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryPartnerProfileRepository extends JpaRepository<DeliveryPartnerProfile, Long> {
    Optional<DeliveryPartnerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByVehicleNumberIgnoreCase(String vehicleNumber);
    boolean existsByLicenseNumberIgnoreCase(String licenseNumber);
    Page<DeliveryPartnerProfile> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
}