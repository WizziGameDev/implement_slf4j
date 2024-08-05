package org.example;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class Java_15_CircuitBreakerRegistry {
    void Hello() {
        log.info("call me");
        throw new IllegalArgumentException("Ups");
    }

    @Test
    void testConfigCircuitBreaker() {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(50) // Menggunakan 50 panggilan terakhir untuk menghitung tingkat kegagalan
                .minimumNumberOfCalls(50) // Circuit Breaker mulai mengevaluasi setelah minimal 50 panggilan
                .failureRateThreshold(50) // Membuka Circuit Breaker jika >50% dari 50 panggilan gagal ke OPEN atau >50% di HALF_OPEN success akan ke CLOSE
                .waitDurationInOpenState(Duration.ofSeconds(2)) // Waktu menunggu agar OPEN menjadi HALF_OPEN
                .permittedNumberOfCallsInHalfOpenState(10) // Jumlah eksekusi yang diperbolehkan ketika di state HALF_OPEN
                .maxWaitDurationInHalfOpenState(Duration.ofSeconds(0)) // Menunggu di HALF_OPEN, jika 0 menunggu tidak terbatas
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("adam", config);

        for (int i = 0; i < 200000; i++) {
            try {
                Runnable decorateRunnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> Hello());
                decorateRunnable.run();
            } catch (Exception e) {
                log.error("Error : {}", e.getMessage());
            }
        }
    }
}