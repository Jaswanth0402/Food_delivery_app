package com.jaswanth.fooddeliveryplatform.notification.service;

import com.jaswanth.fooddeliveryplatform.notification.dto.OrderStatusEvent;
import com.jaswanth.fooddeliveryplatform.notification.dto.RiderLocationEvent;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;

public interface NotificationService {
    void notifyOrderStatusChanged(OrderStatusEvent event);
    void notifyKitchenNewOrder(Long restaurantId, OrderResponse order);
    void streamRiderLocation(RiderLocationEvent event);
    void notifyDriverDispatchOpportunity(Long driverId, OrderResponse order);
}
