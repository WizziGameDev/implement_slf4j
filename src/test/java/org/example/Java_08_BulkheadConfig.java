package org.example;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.w3c.dom.css.CSSUnknownRule;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Slf4j
public class Java_08_BulkheadConfig {

    @SneakyThrows
    public void slow() {
      log.info("P slow");
      Thread.sleep(1_000);
    }

    @Test
    @SneakyThrows
    void testBulkheadConfig() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5) // yang boleh akses sebanyak 25
                .maxWaitDuration(Duration.ofSeconds(5)) // jika penuh tunggu selama 5 detik
                .build();

        Bulkhead bulkhead = Bulkhead.of("Adam", config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).run();
        }

        Thread.sleep(10000);
    }

    @Test
    @SneakyThrows
    void testThreadpool(){
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .coreThreadPoolSize(2) // min
                .maxThreadPoolSize(10) // max
                .keepAliveDuration(Duration.ofSeconds(2)) // Durasi hidup jika thread tidak bekerja
                .queueCapacity(100) // Kapasitas antrian
                .build();

        ThreadPoolBulkhead threadPoolBulkhead = ThreadPoolBulkhead.of("Adam", config);

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(threadPoolBulkhead, () -> slow());
            supplier.get();
        }
        Thread.sleep(20000);
    }
}
