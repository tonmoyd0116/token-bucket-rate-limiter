package com.tonmoyd0116.ratelimiter.store;

public interface BucketStore {
    boolean tryConsume(String client);
}
