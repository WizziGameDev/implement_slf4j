package org.example;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Java_06_RateLimiterRegistry {

    @Test
    void testRateLimiterRegistry() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(2))
                .build();
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
        rateLimiterRegistry.addConfiguration("Adam", rateLimiterConfig);

        // Singleton
        RateLimiter rateLimiter = SingletonRateLimiterRegistry.getInstace().rateLimiter("Adam", SingletonRateLimiterRegistry.updateConfig(10, Duration.ofSeconds(2)));
    }
}

class SingletonRateLimiterRegistry{

    private static final RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();

    private SingletonRateLimiterRegistry(){

    }

    public static RateLimiterRegistry getInstace() {
        return rateLimiterRegistry;
    }

    public static RateLimiterConfig updateConfig(Integer period, Duration durationRefresh){
        return RateLimiterConfig.custom()
                .limitForPeriod(period)
                .limitRefreshPeriod(durationRefresh)
                .build();
    }
}
