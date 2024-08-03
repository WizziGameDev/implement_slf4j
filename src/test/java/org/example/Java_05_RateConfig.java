package org.example;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class Java_05_RateConfig {

    private final AtomicLong counter = new AtomicLong();

    @Test
    void testRateLimiterConfig() {

        RateLimiterConfig limiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(10) // batasan request per periode
                .limitRefreshPeriod(Duration.ofSeconds(2)) // setelah refresh periode kembali ke 0
                .build();

        RateLimiter rateLimiter = RateLimiter.of("a", limiterConfig);

        for (int i = 0; i < 1_000; i++) {
            Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
                long result = counter.incrementAndGet();
                log.info("Result: {}", result);
            });

            runnable.run();
        }
    }
}