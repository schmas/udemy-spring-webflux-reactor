package com.br.schmidt.udemy.reactivespring.server.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    void fluxTest() {
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

    @Test
    void fluxTestElements_WithoutError() {
        final Flux<String> stringFlux =
                Flux.just("Spring", "Spring Boot", "Reactive Spring")
                    .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Spring")
                    .expectNext("Spring Boot")
                    .expectNext("Reactive Spring")
                    .verifyComplete();
    }

    @Test
    void fluxTestElements_WithError1() {
        final Flux<String> stringFlux =
                Flux.just("Spring", "Spring Boot", "Reactive Spring")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Spring", "Spring Boot", "Reactive Spring")
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                            && "Exception Occurred".equals(throwable.getMessage()))
                    .verify();
    }

    @Test
    void fluxTestElements_WithError() {
        final Flux<String> stringFlux =
                Flux.just("Spring", "Spring Boot", "Reactive Spring")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .log();

        StepVerifier.create(stringFlux)
                    .expectNextCount(3)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                            && "Exception Occurred".equals(throwable.getMessage()))
                    .verify();
    }

    @Test
    void monoTest() {
        final Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                    .expectNext("Spring")
                    .verifyComplete();
    }

    @Test
    void monoTest_Error() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                            && "Exception Occurred".equals(throwable.getMessage()))
                    .verify();
    }
}
