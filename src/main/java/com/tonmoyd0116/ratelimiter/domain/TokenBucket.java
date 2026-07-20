//******************Encapsulation Insight****************
// We are implementing Domain Design Principle for TokenBucket which achieves Encapsulation
// Encapsulation does not mean "no logic in the model." It means "the object owns the logic that governs its own state."
// This model helps us achieve the Single Responsibility Principle

package com.tonmoyd0116.ratelimiter.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Getter
public class TokenBucket {
    private final long capacity;
    private final long refillTokens; //The number of tokens to add back to the bucket every refillDuration.
    private final Duration refillDuration;

    private long availableTokens;
    private Instant lastRefillTime;

    public TokenBucket(long capacity, long refillTokens, Duration refillDuration){
        if(capacity<=0) throw new IllegalArgumentException("Bucket Capacity must be greater than 0");
        if(refillTokens<=0) throw new IllegalArgumentException("Refill Tokens must be greater than 0");
        if(refillDuration.isNegative() || refillDuration.isZero()) throw new IllegalArgumentException("Refill Duration must be greater than 0");

        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillDuration = refillDuration;
        this.availableTokens = capacity;
        this.lastRefillTime = Instant.now();

        log.info("Token Bucket Initialized. Tok-Bucket Capacity:{} Refill-Tokens:{} Refill-Duration:{}",
                capacity,
                refillTokens,
                refillDuration.getSeconds());

    }

    /**
     * Attempts to Consume One token per request
     * @return true if a token is consumed per request
     *      else false
     */
    public synchronized boolean tryConsume(){
        refill();
        log.info("Bucket Capacity before consumption:{}",availableTokens);
        if(availableTokens>0){
            availableTokens--;
            log.debug("Token consumed successfully. Remaining tokens:{}",availableTokens );
            log.info("Bucket Capacity after consumption:{}",availableTokens);
            return true;
        }
        log.info("Bucket Capacity after failed consumption:{}",availableTokens);
        log.debug("Token consumption failed. No tokens are available");
        return false;
    }
    /**
     * Refills the bucket based on the elapsed time since last refill
     */
    public void refill(){
        Instant now = Instant.now();

        long elapsedPeriods = Duration.between(lastRefillTime,now).toMillis()/refillDuration.toMillis();

        if(elapsedPeriods<=0) return;

        long tokensToAdd = elapsedPeriods * refillTokens;
        long previousTokens = availableTokens;
        availableTokens = Math.min(capacity,availableTokens+tokensToAdd);
        lastRefillTime = lastRefillTime.plus(
                refillDuration.multipliedBy(elapsedPeriods)
        );

        log.debug(
                "Bucket refilled. Previous={}, Added={}, Current={}",
                previousTokens,
                tokensToAdd,
                availableTokens
        );
    }
}
