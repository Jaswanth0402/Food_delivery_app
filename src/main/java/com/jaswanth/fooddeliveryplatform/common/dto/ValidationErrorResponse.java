package com.jaswanth.fooddeliveryplatform.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String message,
        Map<String,String>errors,
        LocalDateTime timestamp
) {
}
