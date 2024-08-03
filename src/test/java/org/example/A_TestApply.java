package org.example;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class A_TestApply {

    @SneakyThrows
    String task(String e) {
        log.info("Thread ke-" + e);
        throw new IllegalArgumentException("Pbro");
    }

    @Test
    @SneakyThrows
    @DisplayName("Test Combine RateLimiter, Bulkhead, Retry")
    void testSave() {
        RateLimiter rateLimiter = SingletonRateLimiterRegistry.getInstace().rateLimiter("Adam", SingletonRateLimiterRegistry.updateConfig(3, Duration.ofSeconds(1)));
        ThreadPoolBulkhead threadPoolBulkhead = SingletonBulkheadThreadPoolRegistry.getInstance().bulkhead("Adam", SingletonBulkheadThreadPoolRegistry.updateConfig(3, 10, Duration.ofSeconds(1), 100));
        Retry retry = SingletonRetryRagistry.getInstance().retry("Adam", SingletonRetryRagistry.config(3, Duration.ofSeconds(1)));

        // Bulkhead dengan retry
        for (int i = 0; i < 100; i++) {
            Callable<CompletionStage<String>> completionStageCallable = RateLimiter.decorateCallable(rateLimiter, () -> {
                Supplier<CompletionStage<String>> supplier = ThreadPoolBulkhead.decorateSupplier(threadPoolBulkhead, () -> {

                    Function<String, String> decoratedFunction = Retry.decorateFunction(retry, this::task);
                    return decoratedFunction.apply(Thread.currentThread().getName());
                });

                return supplier.get();
            });
            completionStageCallable.call();
        }

        Thread.sleep(10_000);
    }
}
