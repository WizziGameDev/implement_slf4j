package org.example;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.micrometer.Timer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Java_16_Decorator {

    AtomicInteger atomicInteger = new AtomicInteger();

    @SneakyThrows
    void testhello() {
        log.info("Test " + atomicInteger.incrementAndGet());
        throw new IllegalArgumentException("Test Fail-"+atomicInteger);
    }

    @Test
    @SneakyThrows
    void testDecorator() {

        Runnable runnable = Decorators.ofRunnable(() -> testhello())
                .withRateLimiter(SingletonRateLimiterRegistry.getInstace().rateLimiter("Adam", SingletonRateLimiterRegistry.updateConfig(5, Duration.ofSeconds(1), Duration.ofSeconds(3))))
                .withBulkhead(SingletonBulkheadRegistry.getInstance().bulkhead("Adam", SingletonBulkheadRegistry.updateConfig()))
                .withRetry(SingletonRetryRagistry.getInstance().retry("Adam", SingletonRetryRagistry.config(2, Duration.ofMillis(500))))
                .decorate();

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 20; i++) {
            executorService.execute(runnable);
        }

        executorService.awaitTermination(1, TimeUnit.DAYS);
    }
}
