package com.jaswanth.fooddeliveryplatform.notification.controller;

import com.jaswanth.fooddeliveryplatform.delivery.service.DriverLocationService;
import com.jaswanth.fooddeliveryplatform.notification.dto.RiderLocationEvent;
import com.jaswanth.fooddeliveryplatform.notification.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class LocationStreamController {

    private final NotificationService notificationService;
    private final DriverLocationService driverLocationService;

    public LocationStreamController(NotificationService notificationService,
                                    DriverLocationService driverLocationService) {
        this.notificationService = notificationService;
        this.driverLocationService = driverLocationService;
    }

    /**
     * Driver Mobile App sends location packets to: /app/rider/location
     * Payload: { "orderId": 12, "deliveryPartnerId": 2, "latitude": 9.9252, "longitude": 78.1198 }
     */
    @MessageMapping("/rider/location")
    public void handleRiderLocationUpdate(@Payload RiderLocationEvent payload) {
        if (payload == null || payload.latitude() == null || payload.longitude() == null) {
            return;
        }

        BigDecimal lat = payload.latitude();
        BigDecimal lng = payload.longitude();

        // 1. Guard against GPS corruption
        if (lat.doubleValue() < -90.0 || lat.doubleValue() > 90.0 ||
                lng.doubleValue() < -180.0 || lng.doubleValue() > 180.0) {
            return;
        }

        // 2. Update Redis Geospatial index for future candidate dispatching
        if (payload.deliveryPartnerId() != null) {
            driverLocationService.updateDriverLocation(payload.deliveryPartnerId(), lat, lng);
        }

        // 3. Broadcast real-time breadcrumb to customer tracking screen
        RiderLocationEvent enrichedEvent = new RiderLocationEvent(
                payload.orderId(),
                payload.deliveryPartnerId(),
                lat,
                lng,
                LocalDateTime.now()
        );
        notificationService.streamRiderLocation(enrichedEvent);
    }
}