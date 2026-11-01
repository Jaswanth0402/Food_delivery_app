package com.jaswanth.fooddeliveryplatform.delivery.service;

import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerApplyRequest;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerResponse;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface DeliveryPartnerService {

    // Customer flow: apply to become a delivery partner
    DeliveryPartnerResponse apply(DeliveryPartnerApplyRequest request, Long userId);

    // Rider flow: get profile
    DeliveryPartnerResponse getMyProfile(Long userId);

    // Rider flow: toggle online/offline shift status
    DeliveryPartnerResponse toggleOnlineStatus(Long userId, boolean isOnline);

    // Rider flow: update live location coordinates
    DeliveryPartnerResponse updateLocation(Long userId, BigDecimal latitude, BigDecimal longitude);

    // Admin flow: review applicants
    Page<DeliveryPartnerResponse> getPendingApplications(Pageable pageable);

    // Admin flow: approve/reject with automatic role elevation
    DeliveryPartnerResponse updateApprovalStatus(Long profileId, DeliveryPartnerStatusUpdateRequest request);
}
