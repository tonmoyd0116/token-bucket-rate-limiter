package com.tonmoyd0116.ratelimiter.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
@Primary
public class RedisBucketStore implements BucketStore{
    private final StringRedisTemplate redistTemplate;
    
    private static final String BUCKET_KEY = "tokenBuckets";
    private static final String TOKENS_FIELD = "tokens";
    private static final String LAST_REFILL_FIELD = "lastRefill";
    private static final String CAPACITY_FIELD = "capacity";
    private static final String REFILL_RATE_FIELD = "refillRate";

    @Value("${rate.limit.capacity:60}")
    private long DEFAULT_CAPACITY;
    
    @Value("${rate.limit.refill-rate:1}")
    private long DEFAULT_REFILL_RATE;

    @Override
    public boolean tryConsume(String clientId) {
        if (clientId == null || clientId.isBlank()) {
           throw new IllegalArgumentException("Client ID cannot be null");
        }
        
        HashOperations<String, String, String> hashOps = redistTemplate.opsForHash();
        
        // Initialize bucket if it doesn't exist
        if (!hashOps.hasKey(BUCKET_KEY, clientId + ":" + TOKENS_FIELD)) {
           initializeBucket(hashOps, clientId);
           log.info("Creating Token Bucket for ClientId: {}", clientId);
        }
        
        // Get current bucket state
        long currentTokens = Long.parseLong(
           Objects.requireNonNull(hashOps.get(BUCKET_KEY, clientId + ":" + TOKENS_FIELD))
        );
        long lastRefillTime = Long.parseLong(
           Objects.requireNonNull(hashOps.get(BUCKET_KEY, clientId + ":" + LAST_REFILL_FIELD))
        );
        
        // Calculate refill
        long now = System.currentTimeMillis();
        long elapsedMs = now - lastRefillTime;
        long elapsedSeconds = elapsedMs / 1000;
        long tokensToAdd = elapsedSeconds * DEFAULT_REFILL_RATE;
        
        // Refill tokens (capped at capacity)
        long reffilledTokens = Math.min(DEFAULT_CAPACITY, currentTokens + tokensToAdd);
        
        // Try to consume
        if (reffilledTokens > 0) {
           reffilledTokens--;
            
           // Update bucket in Redis
           hashOps.put(BUCKET_KEY, clientId + ":" + TOKENS_FIELD, String.valueOf(reffilledTokens));
           hashOps.put(BUCKET_KEY, clientId + ":" + LAST_REFILL_FIELD, String.valueOf(now));
            
           log.info("Request allowed for clientId: {}. Remaining tokens: {}", clientId, reffilledTokens);
           return true;
        } else {
           log.warn("Request denied for clientId: {}. No tokens available", clientId);
           return false;
        }
    }
    
    private void initializeBucket(HashOperations<String, String, String> hashOps, String clientId) {
        long now = System.currentTimeMillis();
        
        hashOps.put(BUCKET_KEY, clientId + ":" + TOKENS_FIELD, String.valueOf(DEFAULT_CAPACITY));
        hashOps.put(BUCKET_KEY, clientId + ":" + LAST_REFILL_FIELD, String.valueOf(now));
        hashOps.put(BUCKET_KEY, clientId + ":" + CAPACITY_FIELD, String.valueOf(DEFAULT_CAPACITY));
        hashOps.put(BUCKET_KEY, clientId + ":" + REFILL_RATE_FIELD, String.valueOf(DEFAULT_REFILL_RATE));
    }
}
