package com.jaswanth.fooddeliveryplatform.order.service;

import com.jaswanth.fooddeliveryplatform.order.dto.CheckoutRequest;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // Customer operations
    OrderResponse checkout(CheckoutRequest request, Long customerId);
    OrderResponse getOrderByIdForCustomer(Long orderId, Long customerId);
    Page<OrderResponse> getCustomerOrderHistory(Long customerId, Pageable pageable);
    OrderResponse cancelOrder(Long orderId, Long customerId);

    // Restaurant partner operations
    Page<OrderResponse> getRestaurantOrders(Long restaurantId, Long ownerId, Pageable pageable);
    OrderResponse updateOrderStatusByRestaurant(Long restaurantId, Long orderId, OrderStatusUpdateRequest request, Long ownerId);

    // Delivery partner operations
    List<OrderResponse> getAvailableOrdersForDelivery();
    OrderResponse acceptDelivery(Long orderId, Long deliveryPartnerId);
    OrderResponse updateDeliveryStatus(Long orderId, OrderStatusUpdateRequest request, Long deliveryPartnerId);
    List<OrderResponse> getMyActiveDeliveries(Long deliveryPartnerId);
}
