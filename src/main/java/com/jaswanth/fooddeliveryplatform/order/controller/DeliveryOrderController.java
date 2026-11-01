package com.jaswanth.fooddeliveryplatform.order.controller;

import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deliveries")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
@Tag(name = "Delivery Fleet", description = "Available orders discovery, atomic job acceptance, and status advances")
public class DeliveryOrderController {

    private final OrderService orderService;

    public DeliveryOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Discover unassigned orders ready for pickup nearby")
    @GetMapping("/available")
    public ResponseEntity<List<OrderResponse>> getAvailableOrders() {
        return ResponseEntity.ok(orderService.getAvailableOrdersForDelivery());
    }

    @Operation(summary = "Accept an available delivery job (Race-condition protected)")
    @PostMapping("/{orderId}/accept")
    public ResponseEntity<OrderResponse> acceptDelivery(
            @PathVariable Long orderId,
            Authentication authentication) {
        Long deliveryPartnerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.acceptDelivery(orderId, deliveryPartnerId));
    }

    @Operation(summary = "Get active deliveries currently assigned to this rider")
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponse>> getMyActiveDeliveries(Authentication authentication) {
        Long deliveryPartnerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.getMyActiveDeliveries(deliveryPartnerId));
    }

    @Operation(summary = "Advance delivery status: OUT_FOR_DELIVERY -> DELIVERED")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateDeliveryStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            Authentication authentication) {
        Long deliveryPartnerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.updateDeliveryStatus(orderId, request, deliveryPartnerId));
    }
}