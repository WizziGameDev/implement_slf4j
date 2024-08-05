package org.example;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Java_11_TimeLimiterConfig {

    @SneakyThrows
    String test() {
        log.info("Waiting");
        Thread.sleep(3000);
        return "Success";
    }

    @Test
    @SneakyThrows
    void testConfig() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> submit = executorService.submit(this::test);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(2))
                .cancelRunningFuture(true)
                .build();

        TimeLimiter timeLimiter = TimeLimiter.of("Name", config);
        Callable<String> stringCallable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> submit);
        stringCallable.call();
    }
}
