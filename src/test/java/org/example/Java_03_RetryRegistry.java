package org.example;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class Java_03_RetryRegistry {

    // Seperti pooling menggunakan object yang sudah dibuat

    RetryRegistry retryRegistry = RetryRegistry.ofDefaults();

    String dataku (String e, Integer p) {
        log.info(e + ", Retry ke-" + p);
        throw new IllegalArgumentException("Bahaya");
    }

    @Test
    void testRegistry() throws InterruptedException {
        Retry r1 = retryRegistry.retry("Adam");
        Retry r2 = retryRegistry.retry("Adam");

        Assertions.assertSame(r1, r2);

        // Singleton
        Retry bidan1 = SingletonRetryRagistry.getInstance().retry("Bidan");
        Retry bidan2 = SingletonRetryRagistry.getInstance().retry("Bidan");

        Assertions.assertSame(bidan1, bidan2);

        // Thread with Retry
        Retry cobain = SingletonRetryRagistry.getInstance().retry("adam", SingletonRetryRagistry.config(2, Duration.ofSeconds(2)));
        var executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            final var index = i;
            executor.execute(()-> {

                Function<Integer, String> decorateFunction = Retry.decorateFunction(cobain, (e) -> {
                    return dataku(Thread.currentThread().getName(), e);
                });
                decorateFunction.apply(index);
            });
        }

        executor.awaitTermination(1, TimeUnit.DAYS);
    }

    @Test
    void testRegistryConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(20)
                .waitDuration(Duration.ofSeconds(2))
                .build();
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        // Untuk config pada retryRegistry
        retryRegistry.addConfiguration("adam", config);
    }
}

// Singleton
class SingletonRetryRagistry {

    private static final RetryRegistry retryRegistry = RetryRegistry.ofDefaults();

    private SingletonRetryRagistry() {}

    public static RetryRegistry getInstance() {
        return retryRegistry;
    }

    public static RetryConfig config(Integer maxAttempts, Duration waitDuration) {
        // Membuat konfigurasi baru
        return RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(waitDuration)
                .retryExceptions()
                .build();
    }
}