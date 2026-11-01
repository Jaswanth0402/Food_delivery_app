package com.jaswanth.fooddeliveryplatform.delivery.service;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class DriverLocationServiceImpl implements DriverLocationService {

    public static final String DRIVER_GEO_KEY = "drivers:online:geo";
    private final StringRedisTemplate redisTemplate;

    public DriverLocationServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void updateDriverLocation(Long driverId, BigDecimal latitude, BigDecimal longitude) {
        if (driverId == null || latitude == null || longitude == null) {
            return;
        }
        // Spring Data Redis Point: (x = longitude, y = latitude)
        Point point = new Point(longitude.doubleValue(), latitude.doubleValue());
        redisTemplate.opsForGeo().add(DRIVER_GEO_KEY, point, String.valueOf(driverId));
    }

    @Override
    public void removeDriverLocation(Long driverId) {
        if (driverId == null) {
            return;
        }
        // Remove from the underlying sorted set
        redisTemplate.opsForZSet().remove(DRIVER_GEO_KEY, String.valueOf(driverId));
    }

    @Override
    public List<Long> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        if (latitude == null || longitude == null) {
            return Collections.emptyList();
        }

        Point center = new Point(longitude.doubleValue(), latitude.doubleValue());
        Distance distance = new Distance(radiusKm, org.springframework.data.geo.Metrics.KILOMETERS);
        Circle searchZone = new Circle(center, distance);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVER_GEO_KEY, searchZone, args);

        if (results == null || results.getContent().isEmpty()) {
            return Collections.emptyList();
        }

        return results.getContent().stream()
                .map(geoResult -> Long.valueOf(geoResult.getContent().getName()))
                .toList();
    }
}