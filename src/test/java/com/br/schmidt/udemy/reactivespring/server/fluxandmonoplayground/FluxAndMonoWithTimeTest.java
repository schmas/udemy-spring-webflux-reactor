package com.br.schmidt.udemy.reactivespring.server.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    void infiniteSequence() throws InterruptedException {

        final Flux<Long> infiniteFlux =
                Flux.interval(Duration.ofMillis(100))// starts from 0 to ...
                    .log();

        infiniteFlux.subscribe(e -> System.out.println("Value is: " + e));

        Thread.sleep(3000);
    }

    @Test
    void infiniteSequenceTest() {

        final Flux<Long> finiteFlux =
                Flux.interval(Duration.ofMillis(100))
                    .take(3)
                    .log();

        StepVerifier.create(finiteFlux)
                    .expectSubscription()
                    .expectNext(0L, 1L, 2L)
                    .verifyComplete();
    }

    @Test
    void infiniteSequenceMap() {

        final Flux<Integer> finiteFlux =
                Flux.interval(Duration.ofMillis(100))
                    .delayElements(Duration.ofSeconds(1))
                    .map(Long::intValue)
                    .take(3)
                    .log();

        StepVerifier.create(finiteFlux)
                    .expectSubscription()
                    .expectNext(0, 1, 2)
                    .verifyComplete();
    }
}
