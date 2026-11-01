package com.jaswanth.fooddeliveryplatform.order.controller;

import com.jaswanth.fooddeliveryplatform.order.dto.CheckoutRequest;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import com.jaswanth.fooddeliveryplatform.order.service.OrderService;
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

@RestController
@RequestMapping("/api/v1/orders")
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer Orders", description = "Customer checkout, tracking, and cancellation")
public class CustomerOrderController {

    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Checkout active cart into an order snapshot")
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        OrderResponse response = orderService.checkout(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get specific order details owned by customer")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.getOrderByIdForCustomer(orderId, customerId));
    }

    @Operation(summary = "View customer paginated order history")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrderHistory(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.getCustomerOrderHistory(customerId, pageable));
    }

    @Operation(summary = "Cancel order (Permissible while status is PENDING or CONFIRMED before preparation)")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(orderService.cancelOrder(orderId, customerId));
    }
}