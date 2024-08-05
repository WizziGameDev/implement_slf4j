package org.example;

import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Java_10_TimeLimiter {

    // Digunakan untuk membatasi suatu tugas berjalan
    // Dalam bentuk future / completable future

    String test() {
        log.info("Apa kabar");
        return "Broo";
    }

    @Test
    @SneakyThrows
    void testTimer() {
        var executor = Executors.newSingleThreadExecutor();
        Future<String> submit = executor.submit(this::test);

        TimeLimiter timeLimiter = TimeLimiter.ofDefaults("adam");
        Callable<String> stringCallable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> submit);
        stringCallable.call();
    }
}
