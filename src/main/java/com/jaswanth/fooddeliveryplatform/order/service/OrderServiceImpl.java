package com.jaswanth.fooddeliveryplatform.order.service;

import com.jaswanth.fooddeliveryplatform.cart.entity.Cart;
import com.jaswanth.fooddeliveryplatform.cart.entity.CartItem;
import com.jaswanth.fooddeliveryplatform.cart.repository.CartRepository;
import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.util.PhoneNumberFormatter;
import com.jaswanth.fooddeliveryplatform.coupon.entity.Coupon;
import com.jaswanth.fooddeliveryplatform.coupon.service.CouponService;
import com.jaswanth.fooddeliveryplatform.delivery.service.DriverLocationService;
import com.jaswanth.fooddeliveryplatform.notification.dto.OrderStatusEvent;
import com.jaswanth.fooddeliveryplatform.notification.service.NotificationService;
import com.jaswanth.fooddeliveryplatform.order.dto.CheckoutRequest;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderItemResponse;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderStatusUpdateRequest;
import com.jaswanth.fooddeliveryplatform.order.entity.Order;
import com.jaswanth.fooddeliveryplatform.order.entity.OrderItem;
import com.jaswanth.fooddeliveryplatform.order.entity.OrderStatus;
import com.jaswanth.fooddeliveryplatform.order.repository.OrderRepository;
import com.jaswanth.fooddeliveryplatform.payment.entity.Payment;
import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentMethod;
import com.jaswanth.fooddeliveryplatform.payment.entity.PaymentStatus;
import com.jaswanth.fooddeliveryplatform.payment.repository.PaymentRepository;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.MenuItem;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.Restaurant;
import com.jaswanth.fooddeliveryplatform.restaurant.repository.RestaurantRepository;
import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal PACKAGING_FEE = new BigDecimal("20.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("40.00");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.05"); // 5% GST

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;
    private final DriverLocationService driverLocationService;
    private final CouponService couponService;
    private final PhoneNumberFormatter phoneNumberFormatter;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            RestaurantRepository restaurantRepository,
                            UserRepository userRepository,
                            NotificationService notificationService,
                            PaymentRepository paymentRepository,
                            DriverLocationService driverLocationService,
                            CouponService couponService,
                            PhoneNumberFormatter phoneNumberFormatter) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.paymentRepository = paymentRepository;
        this.driverLocationService = driverLocationService;
        this.couponService = couponService;
        this.phoneNumberFormatter = phoneNumberFormatter;
    }

    // =========================================================================
    // Customer Workflows
    // =========================================================================

    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request, Long customerId) {
        Cart cart = cartRepository.findByUserId(customerId)
                .orElseThrow(() -> new BusinessConflictException("Cart not found. Add items to cart before placing an order."));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessConflictException("Cannot checkout with an empty cart.");
        }

        Restaurant restaurant = cart.getRestaurant();
        if (restaurant == null || restaurant.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BusinessConflictException("Cannot checkout. Restaurant is invalid or inactive.");
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + customerId));

        // 1. Calculate price snapshot and validate live item inventory
        BigDecimal itemTotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            MenuItem menuItem = item.getMenuItem();
            if (!menuItem.isAvailable()) {
                throw new BusinessConflictException("Dish '" + menuItem.getName() + "' is out of stock. Please update your cart.");
            }
            BigDecimal lineTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemTotal = itemTotal.add(lineTotal);
        }

        // 2. Evaluate Coupon Discount & Record Usage via CouponService
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = null;
        if (cart.getAppliedCoupon() != null) {
            Coupon coupon = cart.getAppliedCoupon();
            if (coupon.isValid(itemTotal)) {
                discountAmount = coupon.calculateDiscount(itemTotal);
                couponCode = coupon.getCode();
                couponService.recordCouponUsage(coupon.getId());
            }
        }

        BigDecimal discountedSubtotal = itemTotal.subtract(discountAmount).max(BigDecimal.ZERO);
        BigDecimal packagingFee = PACKAGING_FEE;
        BigDecimal deliveryFee = DELIVERY_FEE;
        BigDecimal taxAmount = discountedSubtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = discountedSubtotal.add(packagingFee).add(deliveryFee).add(taxAmount);

        // 3. Format phone to E.164
        String normalizedPhone = phoneNumberFormatter.formatToE164(request.customerPhone());

        // 4. Instantiate Order Entity
        Order order = new Order(
                customer,
                restaurant,
                itemTotal,
                taxAmount,
                deliveryFee,
                packagingFee,
                totalAmount,
                request.deliveryAddress().trim(),
                request.deliveryLatitude(),
                request.deliveryLongitude(),
                normalizedPhone
        );
        order.setDiscountDetails(discountAmount, couponCode);

        // Handle Payment Strategy
        boolean isCod = request.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY;
        order.setOrderStatus(isCod ? OrderStatus.CONFIRMED : OrderStatus.PENDING);

        // 5. Freeze item price snapshots into OrderItem entities
        for (CartItem cartItem : cart.getItems()) {
            MenuItem menuItem = cartItem.getMenuItem();
            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            OrderItem orderItem = new OrderItem(
                    order,
                    menuItem,
                    menuItem.getName(),
                    menuItem.getPrice(),
                    cartItem.getQuantity(),
                    subtotal
            );
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // 6. Create matching Payment entry
        Payment payment = new Payment(savedOrder, customer, request.paymentMethod(), totalAmount);
        if (isCod) {
            payment.setPaymentStatus(PaymentStatus.PENDING); // Will be completed upon physical handover
        }
        paymentRepository.save(payment);

        // 7. Clean and reset cart session
        cart.clearItems();
        cart.setRestaurant(null);
        cart.setAppliedCoupon(null);
        cartRepository.save(cart);

        OrderResponse orderResponse = mapToResponse(savedOrder);

        // 8. Real-Time WebSocket Alerts
        if (isCod) {
            notificationService.notifyKitchenNewOrder(restaurant.getId(), orderResponse);
            notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                    savedOrder.getId(),
                    OrderStatus.CONFIRMED,
                    "Order placed successfully via Cash on Delivery! Kitchen has received your order.",
                    LocalDateTime.now()
            ));
        }

        return orderResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForCustomer(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getCustomerOrderHistory(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessConflictException("Order cannot be cancelled once preparation has started (Current Status: " + order.getOrderStatus() + ").");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);

        // Update payment record if exists
        paymentRepository.findByOrderId(orderId).ifPresent(p -> {
            p.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(p);
        });

        notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                updated.getId(),
                OrderStatus.CANCELLED,
                "Order #" + orderId + " was cancelled by customer.",
                LocalDateTime.now()
        ));

        return mapToResponse(updated);
    }

    // =========================================================================
    // Restaurant Owner Workflows
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getRestaurantOrders(Long restaurantId, Long ownerId, Pageable pageable) {
        verifyRestaurantOwnership(restaurantId, ownerId);
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatusByRestaurant(Long restaurantId, Long orderId, OrderStatusUpdateRequest request, Long ownerId) {
        verifyRestaurantOwnership(restaurantId, ownerId);

        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId + " for this restaurant."));

        OrderStatus targetStatus = request.status();

        if (targetStatus != OrderStatus.CONFIRMED
                && targetStatus != OrderStatus.PREPARING
                && targetStatus != OrderStatus.READY_FOR_PICKUP
                && targetStatus != OrderStatus.CANCELLED) {
            throw new BusinessConflictException("Invalid state transition target for restaurant: " + targetStatus);
        }

        if (!order.getOrderStatus().canTransitionTo(targetStatus)) {
            throw new BusinessConflictException("Cannot transition order from " + order.getOrderStatus() + " to " + targetStatus);
        }

        order.setOrderStatus(targetStatus);
        Order savedOrder = orderRepository.save(order);

        // If kitchen marks food as packed and ready, trigger Redis geospatial driver dispatch
        if (targetStatus == OrderStatus.READY_FOR_PICKUP) {
            BigDecimal restLat = order.getRestaurant().getLatitude();
            BigDecimal restLng = order.getRestaurant().getLongitude();

            if (restLat != null && restLng != null) {
                // Redis Geospatial query for online drivers within 5.0 km
                List<Long> nearbyDriverIds = driverLocationService.findNearbyDrivers(restLat, restLng, 5.0);

                OrderResponse responseSnapshot = mapToResponse(savedOrder);
                for (Long driverId : nearbyDriverIds) {
                    notificationService.notifyDriverDispatchOpportunity(driverId, responseSnapshot);
                }
            }
        }

        notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                savedOrder.getId(),
                targetStatus,
                "Order status updated to: " + targetStatus,
                LocalDateTime.now()
        ));

        return mapToResponse(savedOrder);
    }

    // =========================================================================
    // Delivery Partner Workflows
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAvailableOrdersForDelivery() {
        return orderRepository.findByOrderStatusAndDeliveryPartnerIsNull(OrderStatus.READY_FOR_PICKUP)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse acceptDelivery(Long orderId, Long deliveryPartnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        User driver = userRepository.findById(deliveryPartnerId)
                .orElseThrow(() -> new UserNotFoundException("Delivery partner not found with id: " + deliveryPartnerId));

        if (!driver.getRole().contains(Role.DELIVERY_PARTNER)) {
            throw new AccessDeniedException("User is not an active delivery partner.");
        }

        if (order.getOrderStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BusinessConflictException("Order is not available for pickup (Current status: " + order.getOrderStatus() + ").");
        }

        // Atomic DB assignment preventing race conditions between concurrent drivers
        int affectedRows = orderRepository.assignDriverIfUnassigned(orderId, deliveryPartnerId, OrderStatus.READY_FOR_PICKUP);
        if (affectedRows == 0) {
            throw new BusinessConflictException("Order has already been accepted by another delivery partner.");
        }

        // Refresh entity state after atomic update
        Order acceptedOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                acceptedOrder.getId(),
                OrderStatus.READY_FOR_PICKUP,
                "Delivery partner assigned: " + driver.getFullName(),
                LocalDateTime.now()
        ));

        return mapToResponse(acceptedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateDeliveryStatus(Long orderId, OrderStatusUpdateRequest request, Long deliveryPartnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getDeliveryPartner() == null || !order.getDeliveryPartner().getId().equals(deliveryPartnerId)) {
            throw new AccessDeniedException("You are not assigned to this delivery.");
        }

        OrderStatus targetStatus = request.status();
        if (targetStatus != OrderStatus.OUT_FOR_DELIVERY && targetStatus != OrderStatus.DELIVERED) {
            throw new BusinessConflictException("Invalid delivery transition state: " + targetStatus);
        }

        if (!order.getOrderStatus().canTransitionTo(targetStatus)) {
            throw new BusinessConflictException("Cannot transition order from " + order.getOrderStatus() + " to " + targetStatus);
        }

        order.setOrderStatus(targetStatus);

        // If order delivered, settle Cash On Delivery payment
        if (targetStatus == OrderStatus.DELIVERED) {
            paymentRepository.findByOrderId(orderId).ifPresent(p -> {
                if (p.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
                    p.setPaymentStatus(PaymentStatus.SUCCESS);
                    paymentRepository.save(p);
                }
            });
        }

        Order savedOrder = orderRepository.save(order);

        notificationService.notifyOrderStatusChanged(new OrderStatusEvent(
                savedOrder.getId(),
                targetStatus,
                "Delivery status updated to: " + targetStatus,
                LocalDateTime.now()
        ));

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyActiveDeliveries(Long deliveryPartnerId) {
        List<OrderStatus> activeStatuses = List.of(OrderStatus.READY_FOR_PICKUP, OrderStatus.OUT_FOR_DELIVERY);
        return orderRepository.findByDeliveryPartnerIdAndOrderStatusIn(deliveryPartnerId, activeStatuses)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private void verifyRestaurantOwnership(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        Long actualOwnerId = (restaurant.getOwner() != null) ? restaurant.getOwner().getId() : restaurant.getOwnerId();
        if (!actualOwnerId.equals(ownerId)) {
            throw new AccessDeniedException("You do not own this restaurant.");
        }
    }

    private OrderResponse mapToResponse(Order order) {
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
                order.getDiscountAmount(),
                order.getCouponCode(),
                order.getTaxAmount(),
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