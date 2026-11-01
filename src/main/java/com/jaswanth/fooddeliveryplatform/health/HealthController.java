package com.jaswanth.fooddeliveryplatform.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public Map<String,String> health(){
        return Map.of(
                "status","UP",
                "service","food-delivery-platfdrm"
        );
    }

    @GetMapping("/api/v1/health/value")
    public Map<String,String> healthValue(){
        return Map.of(
                "status","UP",
                "service","food-delivery-platfdrm dependence"
        );
    }
}
