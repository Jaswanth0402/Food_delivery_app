package com.jaswanth.fooddeliveryplatform.delivery.controller;

import com.jaswanth.fooddeliveryplatform.delivery.dto.DriverLocationPing;
import com.jaswanth.fooddeliveryplatform.delivery.dto.NearbyDriverResponse;
import com.jaswanth.fooddeliveryplatform.delivery.service.DeliveryTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deliveries/tracking")
@Tag(name = "Delivery Tracking", description = "High-throughput Redis Geospatial & Live Telemetry APIs")
public class DeliveryTrackingController {

    private final DeliveryTrackingService deliveryTrackingService;

    public DeliveryTrackingController(DeliveryTrackingService deliveryTrackingService) {
        this.deliveryTrackingService = deliveryTrackingService;
    }

    @PostMapping("/orders/{orderId}/ping")
    @Operation(summary = "Rider updates their live GPS location during an active delivery")
    public ResponseEntity<Map<String, Object>> pingOrderLocation(
            @PathVariable Long orderId,
            @Valid @RequestBody DriverLocationPing ping) {

        deliveryTrackingService.recordDriverLocation(orderId, ping);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Location recorded successfully",
                "driverId", ping.driverId(),
                "orderId", orderId,
                "latitude", ping.latitude(),
                "longitude", ping.longitude()
        ));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find all online delivery riders within a specified radius (km) of coordinates")
    public ResponseEntity<List<NearbyDriverResponse>> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        return ResponseEntity.ok(deliveryTrackingService.findDriversNearLocation(latitude, longitude, radiusKm));
    }

    @DeleteMapping("/drivers/{driverId}")
    @Operation(summary = "Remove driver from active spatial pool when going offline")
    public ResponseEntity<Map<String, Object>> removeDriver(@PathVariable Long driverId) {
        deliveryTrackingService.removeDriver(driverId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Driver removed from active spatial pool",
                "driverId", driverId
        ));
    }
}