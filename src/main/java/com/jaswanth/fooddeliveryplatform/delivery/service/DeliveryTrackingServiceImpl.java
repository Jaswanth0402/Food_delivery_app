package com.jaswanth.fooddeliveryplatform.delivery.service;

import com.jaswanth.fooddeliveryplatform.delivery.dto.DriverLocationPing;
import com.jaswanth.fooddeliveryplatform.delivery.dto.NearbyDriverResponse;
import com.jaswanth.fooddeliveryplatform.delivery.service.DeliveryTrackingService;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DeliveryTrackingServiceImpl implements DeliveryTrackingService {

    public static final String DRIVER_GEO_KEY = "drivers:online:geo";

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public DeliveryTrackingServiceImpl(StringRedisTemplate redisTemplate,
                                       SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void recordDriverLocation(Long orderId, DriverLocationPing ping) {
        // Point parameters: (x = longitude, y = latitude)
        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY,
                new Point(ping.longitude(), ping.latitude()),
                String.valueOf(ping.driverId())
        );

        if (orderId != null) {
            Map<String, Object> payload = Map.of(
                    "orderId", orderId,
                    "driverId", ping.driverId(),
                    "latitude", ping.latitude(),
                    "longitude", ping.longitude(),
                    "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend( "/topic/orders/" + orderId + "/tracking",  Optional.of(payload));
        }
    }

    @Override
    public List<NearbyDriverResponse> findDriversNearLocation(double latitude, double longitude, double radiusKm) {
        Point center = new Point(longitude, latitude);
        Distance radius = new Distance(radiusKm, org.springframework.data.geo.Metrics.KILOMETERS);
        Circle area = new Circle(center, radius);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVER_GEO_KEY, area, args);

        List<NearbyDriverResponse> responseList = new ArrayList<>();
        if (results != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                RedisGeoCommands.GeoLocation<String> content = result.getContent();
                double dist = result.getDistance().getValue();
                Point point = content.getPoint();

                responseList.add(new NearbyDriverResponse(
                        Long.valueOf(content.getName()),
                        dist,
                        point != null ? point.getY() : null, // Latitude
                        point != null ? point.getX() : null  // Longitude
                ));
            }
        }
        return responseList;
    }

    @Override
    public void removeDriver(Long driverId) {
        redisTemplate.opsForZSet().remove(DRIVER_GEO_KEY, String.valueOf(driverId));
    }
}