package com.jaswanth.fooddeliveryplatform.payment.controller;

import com.jaswanth.fooddeliveryplatform.payment.dto.GatewayWebhookPayload;
import com.jaswanth.fooddeliveryplatform.payment.dto.InitiatePaymentRequest;
import com.jaswanth.fooddeliveryplatform.payment.dto.PaymentResponse;
import com.jaswanth.fooddeliveryplatform.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment initiation, order payment status, and gateway webhooks")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Customer initiates payment for their PENDING order")
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request,
            Authentication authentication) {
        Long customerId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(paymentService.initiatePayment(request, customerId));
    }

    @Operation(summary = "Fetch payment details by Order ID")
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId,
            Authentication authentication) {
        Long customerId = getAuthenticatedCustomerId(authentication);
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId, customerId));
    }

    @Operation(summary = "Public Webhook endpoint called by Payment Gateway")
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleGatewayWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", defaultValue = "mock_secret_sig") String signature,
            @Valid @RequestBody GatewayWebhookPayload payload) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    private Long getAuthenticatedCustomerId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("User not authenticated.");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException("Invalid authenticated user ID format.");
        }
    }
}