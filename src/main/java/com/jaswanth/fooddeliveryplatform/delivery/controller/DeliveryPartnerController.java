package com.jaswanth.fooddeliveryplatform.delivery.controller;

import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerApplyRequest;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerResponse;
import com.jaswanth.fooddeliveryplatform.delivery.dto.DeliveryPartnerStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.delivery.service.DeliveryPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/delivery-partners")
@Tag(name = "Delivery Fleet", description = "Driver onboarding, KYC verification, shift status, and live GPS heartbeat")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService) {
        this.deliveryPartnerService = deliveryPartnerService;
    }

    @Operation(summary = "Submit Delivery Partner KYC application")
    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeliveryPartnerResponse> apply(
            @Valid @RequestBody DeliveryPartnerApplyRequest request,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        DeliveryPartnerResponse response = deliveryPartnerService.apply(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "View current driver profile")
    @GetMapping("/me")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryPartnerResponse> getMyProfile(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(deliveryPartnerService.getMyProfile(userId));
    }

    @Operation(summary = "Shift toggle: Go Online / Offline")
    @PatchMapping("/shift")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryPartnerResponse> toggleOnlineStatus(
            @RequestParam boolean isOnline,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(deliveryPartnerService.toggleOnlineStatus(userId, isOnline));
    }

    @Operation(summary = "Live GPS heartbeat (Rider App ping updates Redis Geo index)")
    @PatchMapping("/location")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryPartnerResponse> updateLocation(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(deliveryPartnerService.updateLocation(userId, latitude, longitude));
    }

    @Operation(summary = "Admin: List pending KYC submissions")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryPartnerResponse>> getPendingApplications(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(deliveryPartnerService.getPendingApplications(pageable));
    }

    @Operation(summary = "Admin: Approve or reject delivery partner application")
    @PatchMapping("/{profileId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryPartnerResponse> updateApprovalStatus(
            @PathVariable Long profileId,
            @Valid @RequestBody DeliveryPartnerStatusUpdateRequest request) {
        return ResponseEntity.ok(deliveryPartnerService.updateApprovalStatus(profileId, request));
    }
}