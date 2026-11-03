package com.jaswanth.fooddeliveryplatform.delivery.service;

import com.jaswanth.fooddeliveryplatform.delivery.dto.DriverLocationPing;
import com.jaswanth.fooddeliveryplatform.delivery.dto.NearbyDriverResponse;

import java.util.List;

public interface DeliveryTrackingService {

    void recordDriverLocation(Long orderId, DriverLocationPing ping);

    List<NearbyDriverResponse> findDriversNearLocation(double latitude, double longitude, double radiusKm);

    void removeDriver(Long driverId);
}