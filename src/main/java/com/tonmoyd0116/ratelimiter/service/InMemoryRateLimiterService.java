package com.tonmoyd0116.ratelimiter.service;

import com.tonmoyd0116.ratelimiter.domain.TokenBucket;
import com.tonmoyd0116.ratelimiter.store.BucketStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Service
public class InMemoryRateLimiterService implements RateLimiterService{
    private final BucketStore bucketStore;

    @Override
    public boolean allowRequest(String clientId) {
        if(clientId==null || clientId.isBlank()){
            throw new IllegalArgumentException("Client Id cannot be null");
        }

        boolean allowed = bucketStore.tryConsume(clientId);
        if(allowed){
            log.info("Request allowed for clientId:{}",clientId);
        }else{
            log.warn("Request denied for clientId:{}",clientId);
        }
        return allowed;
    }
}
