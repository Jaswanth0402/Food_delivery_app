package com.jaswanth.fooddeliveryplatform.delivery.service;

import java.math.BigDecimal;
import java.util.List;

public interface DriverLocationService {

    // Registers or updates rider coordinate in Redis
    void updateDriverLocation(Long driverId, BigDecimal latitude, BigDecimal longitude);

    // Evicts rider from Redis upon shift logout or disconnection
    void removeDriverLocation(Long driverId);

    // Returns ordered driver IDs within radiusKm of target coordinates
    List<Long> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, double radiusKm);
}