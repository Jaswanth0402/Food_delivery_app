package com.jaswanth.fooddeliveryplatform.delivery.dto;

public record NearbyDriverResponse(
        Long driverId,
        Double distanceKm,
        Double latitude,
        Double longitude
) {}