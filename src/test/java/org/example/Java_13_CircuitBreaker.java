package org.example;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class Java_13_CircuitBreaker {

    /*
    * Cara kerja:
    * Saat code running dan success akan close seperti circuit untuk lampu
    * Jika gagal dengan menentukan rate gagal >50% dengan 10 req dan 5 gagal circuit akan open dan semua request akan ditolak
    * Jika sudah open nantinya kita menentukan berapa durasi gagal misal 15 detik dan akan menuju ke half open
    * Di half open akan melakukan request untuk menuju close jika success code nya >50% dari yang ditentukan maka circuit akan ke status close
    * */

    void Hello() {
        log.info("call me");
        throw new IllegalArgumentException("Ups");
    }

    @Test
    void testCircuitBreaker() {

        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("Adam");

        for (int i = 0; i < 200; i++) {
            try {
                Runnable decorateRunnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> Hello());
                decorateRunnable.run();
            } catch (Exception e) {
                log.error("Error : {}", e.getMessage());
            }

        }
    }
}
