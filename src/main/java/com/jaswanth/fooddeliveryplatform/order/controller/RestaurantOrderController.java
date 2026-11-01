package com.jaswanth.fooddeliveryplatform.order.controller;

import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/orders")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
@Tag(name = "Kitchen Management", description = "Restaurant live order queue and food preparation status transitions")
public class RestaurantOrderController {

    private final OrderService orderService;

    public RestaurantOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "View live kitchen queue for a specific restaurant")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getRestaurantOrders(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        Long ownerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, ownerId, pageable));
    }

    @Operation(summary = "Advance kitchen status: CONFIRMED -> PREPARING -> READY_FOR_PICKUP (or CANCELLED)")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateKitchenStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            Authentication authentication) {
        Long ownerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.updateOrderStatusByRestaurant(restaurantId, orderId, request, ownerId));
    }
}