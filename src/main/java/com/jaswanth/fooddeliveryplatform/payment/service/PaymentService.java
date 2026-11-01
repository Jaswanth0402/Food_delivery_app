package com.jaswanth.fooddeliveryplatform.payment.service;

import com.jaswanth.fooddeliveryplatform.payment.dto.GatewayWebhookPayload;
import com.jaswanth.fooddeliveryplatform.payment.dto.InitiatePaymentRequest;
import com.jaswanth.fooddeliveryplatform.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiatePayment(InitiatePaymentRequest request, Long customerId);
    PaymentResponse getPaymentByOrderId(Long orderId, Long customerId);
    void handleWebhook(GatewayWebhookPayload payload, String signature);
}