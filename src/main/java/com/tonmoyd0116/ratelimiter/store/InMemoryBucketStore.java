package com.tonmoyd0116.ratelimiter.store;

import com.tonmoyd0116.ratelimiter.domain.TokenBucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class InMemoryBucketStore implements BucketStore {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private static final long CAPACITY = 60;
    private static final long REFILL_TOKEN = 1;
    private static final Duration REFILL_DURATION = Duration.ofSeconds(1);
    @Override
    public boolean tryConsume(String clientId){
        TokenBucket bucket = buckets.computeIfAbsent(
          clientId,
          id -> {
              log.info("Creating Token Bucket for ClientId:{}",id);

              return new TokenBucket(
                CAPACITY,
                REFILL_TOKEN,
                REFILL_DURATION
              );
          }
        );

        return bucket.tryConsume();
    }
}
