package org.example;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Slf4j
public class Java_07_Bulkhead {

    @SneakyThrows
    public void slow() {
        log.info("Slow");
        Thread.sleep(5_000L);
    }

    @Test
    void testSemaphore() throws InterruptedException {
        // Bulkhead dengan semaphore
        Bulkhead bulkhead = Bulkhead.ofDefaults("pp");

        for (int i = 0; i < 1_000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testFixedThread() {
        // Bulkhead dengan Threadpool
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("d");

        for (int i = 0; i < 1_000; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }
    }
}