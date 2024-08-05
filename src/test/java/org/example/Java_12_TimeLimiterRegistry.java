package org.example;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Java_12_TimeLimiterRegistry {

    @SneakyThrows
    String love() {
        log.info("Waiting result...");
        return "Done";
    }

    @Test
    @SneakyThrows
    void testTimeLimiterRegistry() {

        var executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(this::love);

        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(1))
                .cancelRunningFuture(true)
                .build();

        TimeLimiter limiter = registry.timeLimiter("Adam", config);

        Callable<String> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);
        callable.call();
    }
}

class SingletonTimeLimiterRegistry{
    private static final TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();

    private SingletonTimeLimiterRegistry() {}

    public static TimeLimiterRegistry getInstance() {
        return registry;
    }

    public static TimeLimiterConfig updateConfig(Duration duration, Boolean stop) {
        return TimeLimiterConfig.custom()
                .timeoutDuration(duration)
                .cancelRunningFuture(stop)
                .build();
    }
}
