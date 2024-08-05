package org.example;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class A_TestApply {

    AtomicInteger atomicInteger = new AtomicInteger();

    @SneakyThrows
    String task(String e) {
        log.info("Executing task in thread: " + e + ", attempt: " + atomicInteger.incrementAndGet());
        throw new IllegalArgumentException("Simulated task failure");
    }

    @Test
    @SneakyThrows
    @DisplayName("Test Combine RateLimiter, Bulkhead, Retry")
    void testSave() {
        RateLimiter rateLimiter = SingletonRateLimiterRegistry.getInstace().rateLimiter("Adam", SingletonRateLimiterRegistry.updateConfig(10, Duration.ofSeconds(1)));
        ThreadPoolBulkhead threadPoolBulkhead = SingletonBulkheadThreadPoolRegistry.getInstance().bulkhead("Adam", SingletonBulkheadThreadPoolRegistry.updateConfig(5, 5, Duration.ofSeconds(1), 100));
        Retry retry = SingletonRetryRagistry.getInstance().retry("Adam", SingletonRetryRagistry.config(1, Duration.ofSeconds(2)));
        TimeLimiter limiter = SingletonTimeLimiterRegistry.getInstance().timeLimiter("Adam", SingletonTimeLimiterRegistry.updateConfig(Duration.ofSeconds(2), true));
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                try {
                    Callable<CompletionStage<String>> completionStageCallable = RateLimiter.decorateCallable(rateLimiter, () -> {
                        Supplier<CompletionStage<String>> supplier = ThreadPoolBulkhead.decorateSupplier(threadPoolBulkhead, () -> {
                            log.info("Thread {} is in bulkhead", Thread.currentThread().getName());
                            Function<String, String> decoratedFunction = Retry.decorateFunction(retry, this::task);
                            return decoratedFunction.apply(Thread.currentThread().getName());
                        });
                        return supplier.get();
                    });
                    completionStageCallable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
}
