package org.example;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Slf4j
public class Java_09_BulkheadRegistry {

    @SneakyThrows
    void hello() {
        log.info("P inpo");
        Thread.sleep(2_000);
    }

    @Test
    @SneakyThrows
    void testRegistryThreadPool() {
        ThreadPoolBulkhead instance = SingletonBulkheadThreadPoolRegistry.getInstance().bulkhead("Adam", SingletonBulkheadThreadPoolRegistry.updateConfig(2, 10, Duration.ofSeconds(1), 100)); // get instance

        for (int i = 0; i < 100; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(instance, () -> hello());
            supplier.get();
        }

        Thread.sleep(10_000L);
    }
}

class SingletonBulkheadRegistry {

    private static final BulkheadRegistry bulkheadRegistry = BulkheadRegistry.ofDefaults();

    private static final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry = ThreadPoolBulkheadRegistry.ofDefaults();

    private SingletonBulkheadRegistry() {

    }

    public static BulkheadRegistry getInstance() {
        return bulkheadRegistry;
    }

    public static BulkheadConfig updateConfig() {
        return BulkheadConfig.custom()
                .maxConcurrentCalls(20)
                .maxWaitDuration(Duration.ofSeconds(2))
                .build();
    }
}

class SingletonBulkheadThreadPoolRegistry {

    private static final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry = ThreadPoolBulkheadRegistry.ofDefaults();

    private SingletonBulkheadThreadPoolRegistry() {

    }

    public static ThreadPoolBulkheadRegistry getInstance() {
        return threadPoolBulkheadRegistry;
    }

    public static ThreadPoolBulkheadConfig updateConfig(Integer core, Integer max, Duration duration, Integer capacity) {
        return ThreadPoolBulkheadConfig.custom()
                .coreThreadPoolSize(core)
                .maxThreadPoolSize(max)
                .keepAliveDuration(duration)
                .queueCapacity(capacity)
                .build();
    }
}