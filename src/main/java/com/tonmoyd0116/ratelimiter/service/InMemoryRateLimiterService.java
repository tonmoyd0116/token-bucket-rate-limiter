package com.tonmoyd0116.ratelimiter.service;

import com.tonmoyd0116.ratelimiter.domain.TokenBucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryRateLimiterService implements RateLimiterService{

    private final ConcurrentHashMap<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    private static final long CAPACITY = 60;
    private static final long REFILL_TOKENS = 1;
    private static final Duration REFILL_DURATION = Duration.ofSeconds(1);

    @Override
    public boolean allowRequest(String clientId) {
        log.info("Creating Rate Limiting for ClientId:{}",clientId);

        if(clientId==null || clientId.isBlank()) throw new IllegalArgumentException("Client ID cannot be empty");

        TokenBucket tokenBucket = tokenBuckets.computeIfAbsent(
                clientId,
                id->{
                    log.info("Creating token but for ClientId:{}",id);

                    return new TokenBucket(
                            CAPACITY,
                            REFILL_TOKENS,
                            REFILL_DURATION
                    );
                }
        );
        boolean allowed = tokenBucket.tryConsume();
        if(allowed){
            log.info("Request allowed for Client:{}",clientId);
        }else{
            log.info("Rate limit exceeded for Client:{}",clientId);
        }

        return allowed;
    }
}
