package com.br.schmidt.udemy.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    void fluxtest() {
        final Flux<String> stringFlux =
                Flux.just("Spring", "Spring Boot", "Reactive Spring")
                    // .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .concatWith(Flux.just("After Error"))
                    .log();

        stringFlux
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Completed")
                );

    }
}
