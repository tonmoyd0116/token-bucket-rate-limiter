package com.tonmoyd0116.ratelimiter.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedisBucketStore implements BucketStore{
    private final StringRedisTemplate redistTemplate;
    private final RedisScript<Long> redisScript;
    @Override
    public boolean tryConsume(String clientId){
        throw new UnsupportedOperationException("No functions implemented yet");
    }
}
