package com.tonmoyd0116.ratelimiter.dto;

public record RatelimiterResponse(
        String clientId,
        Boolean allowed
) {
}
