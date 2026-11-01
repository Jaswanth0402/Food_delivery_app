package com.jaswanth.fooddeliveryplatform.notification.service;

import com.jaswanth.fooddeliveryplatform.notification.dto.OrderStatusEvent;
import com.jaswanth.fooddeliveryplatform.notification.dto.RiderLocationEvent;
import com.jaswanth.fooddeliveryplatform.order.dto.OrderResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyOrderStatusChanged(OrderStatusEvent event) {
        // Pushes to: /topic/orders/{orderId}
        String destination = "/topic/orders/" + event.orderId();
        messagingTemplate.convertAndSend(destination, event);
    }

    @Override
    public void notifyKitchenNewOrder(Long restaurantId, OrderResponse order) {
        // Pushes to: /topic/restaurants/{restaurantId}/orders
        String destination = "/topic/restaurants/" + restaurantId + "/orders";
        messagingTemplate.convertAndSend(destination, order);
    }

    @Override
    public void streamRiderLocation(RiderLocationEvent event) {
        // Pushes to: /topic/orders/{orderId}/location
        String destination = "/topic/orders/" + event.orderId() + "/location";
        messagingTemplate.convertAndSend(destination, event);
    }
    @Override
    public void notifyDriverDispatchOpportunity(Long driverId, OrderResponse order) {
        // Pushes dispatch invitation to: /topic/drivers/{driverId}/jobs
        String destination = "/topic/drivers/" + driverId + "/jobs";
        messagingTemplate.convertAndSend(destination, order);
    }
}