package com.jaswanth.fooddeliveryplatform.payment.service;

import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.notification.dto.OrderStatusEvent;
import com.jaswanth.fooddeliveryplatform.notification.service.NotificationService;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderItemResponse;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import com.jaswanth.fooddeliveryplatform.order.entity.Order;
import com.jaswanth.fooddeliveryplatform.order.entity.OrderStatus;
import com.jaswanth.fooddeliveryplatform.order.repository.OrderRepository;
import com.jaswanth.fooddeliveryplatform.payment.dto.GatewayWebhookPayload;
import com.jaswanth.fooddeliveryplatform.payment.dto.InitiatePaymentRequest;
import com.jaswanth.fooddeliveryplatform.payment.dto.PaymentResponse;
import com.jaswanth.fooddeliveryplatform.payment.entity.Payment;
import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentMethod;
import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentStatus;
import com.jaswanth.fooddeliveryplatform.payment.repository.PaymentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, NotificationService notificationService,  OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request, Long customerId) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.orderId()));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to pay for this order.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BusinessConflictException("Payment can only be initiated for orders in PENDING status. Current status: " + order.getOrderStatus());
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseGet(() -> new Payment(order, order.getCustomer(), request.paymentMethod(), order.getTotalAmount()));

        payment.setPaymentMethod(request.paymentMethod());

        if (request.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setGatewayOrderId(null);

            // Cash On Delivery immediately confirms order into the kitchen queue
            order.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            // Push notifications for COD flow
            notificationService.notifyKitchenNewOrder(order.getRestaurant().getId(), mapToOrderResponse(order));
            notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                    order.getId(),
                    OrderStatus.CONFIRMED,
                    "Order placed via Cash on Delivery! Kitchen has received your order.",
                    LocalDateTime.now()
            ));
        } else {
            // Online Gateway Flow (Razorpay / Stripe)
            String mockGatewayOrderId = "order_mock_" + UUID.randomUUID().toString().substring(0, 8);
            payment.setGatewayOrderId(mockGatewayOrderId);
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId, Long customerId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order id: " + orderId));

        if (!payment.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("Access denied to view this payment.");
        }

        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public void handleWebhook(GatewayWebhookPayload payload, String signature) {
        // 1. Signature check (Mock validation or HMAC-SHA256 in production)
        if (signature == null || signature.isBlank()) {
            throw new BusinessConflictException("Missing or invalid webhook signature.");
        }

        Payment payment = paymentRepository.findByGatewayOrderId(payload.gatewayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for gateway order: " + payload.gatewayOrderId()));

        // 2. IDEMPOTENCY GUARD: Do not process duplicate webhooks if payment already succeeded
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        Order order = payment.getOrder();

        if ("payment.captured".equalsIgnoreCase(payload.event()) || "payment.success".equalsIgnoreCase(payload.event())) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setGatewayPaymentId(payload.gatewayPaymentId());
            paymentRepository.save(payment);

            // Transition Order: PENDING -> CONFIRMED
            order.setOrderStatus(OrderStatus.CONFIRMED);
            Order savedOrder = orderRepository.save(order);

            // Broadcast real-time kitchen and customer alerts
            notificationService.notifyKitchenNewOrder(savedOrder.getRestaurant().getId(), mapToOrderResponse(savedOrder));
            notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                    savedOrder.getId(),
                    OrderStatus.CONFIRMED,
                    "Payment received! Kitchen has confirmed your order.",
                    LocalDateTime.now()
            ));

        } else if ("payment.failed".equalsIgnoreCase(payload.event())) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(payload.failureReason());
            paymentRepository.save(payment);

            notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                    order.getId(),
                    order.getOrderStatus(),
                    "Payment failed: " + payload.failureReason(),
                    LocalDateTime.now()
            ));
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getGatewayOrderId()
        );
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getMenuItem().getId(),
                        item.getItemName(),
                        item.getPriceAtOrder(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .toList();

        Long deliveryPartnerId = order.getDeliveryPartner() != null ? order.getDeliveryPartner().getId() : null;

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getFullName(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                deliveryPartnerId,
                order.getOrderStatus(),
                order.getItemTotal(),
                order.getTaxAmount(),
                order.getCouponCode(),
                order.getDiscountAmount(),
                order.getDeliveryFee(),
                order.getPackagingFee(),
                order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getDeliveryLatitude(),
                order.getDeliveryLongitude(),
                order.getCustomerPhone(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
