package org.example;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

@Slf4j
public class Java_01_RetryTest {

    void testHello() {
      log.info("Hello");
      throw new IllegalArgumentException("Ups error");
    }

    @Test
    void testRetryRunnable() {
        Retry retry = Retry.ofDefaults("example");
        Runnable runnable = Retry.decorateRunnable(retry, () -> testHello());
        runnable.run();
    }

    String hello(String bro) {
        log.info("Haiyaa");
        throw new IllegalArgumentException("salah " + bro);
    }

    @Test
    void testDecorateSupplier() {
        Retry retry= Retry.ofDefaults("adam");
        Function<String, String> function = Retry.decorateFunction(retry, (e) -> hello(e));
        function.apply("Santay");
    }
}
