package com.tonmoyd0116.ratelimiter.service;

public interface RateLimiterService {
    boolean allowRequest(String clientId);
}
