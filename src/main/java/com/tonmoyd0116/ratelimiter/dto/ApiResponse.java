package com.tonmoyd0116.ratelimiter.dto;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        int statusCode,
        String message,
        Instant timestamp,
        T data
) {
}
