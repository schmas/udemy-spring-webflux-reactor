package com.br.schmidt.udemy.reactivespring.server.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {

    private List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    void fluxUsingIterable() {
        final Flux<String> namesFlux = Flux.fromIterable(names);

        StepVerifier.create(namesFlux.log())
                    .expectNext("adam", "anna", "jack", "jenny")
                    .verifyComplete();
    }

    @Test
    void fluxUsingArray() {
        final Flux<String> namesFlux = Flux.fromArray(names.toArray(new String[]{}));

        StepVerifier.create(namesFlux.log())
                    .expectNext("adam", "anna", "jack", "jenny")
                    .verifyComplete();
    }

    @Test
    void fluxUsingStream() {
        final Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux.log())
                    .expectNext("adam", "anna", "jack", "jenny")
                    .verifyComplete();
    }

    @Test
    void monoUsingJustOrEmpty() {
        final Mono<String> mono = Mono.justOrEmpty(null); // Mono.Empty();

        StepVerifier.create(mono.log())
                    .verifyComplete();
    }

    @Test
    void monoUsingSupplier() {
        final Mono<String> stringMono = Mono.fromSupplier(() -> "adam");

        StepVerifier.create(stringMono.log())
                    .expectNext("adam")
                    .verifyComplete();
    }

    @Test
    void fluxUsingRange() {
        final Flux<Integer> integerFlux = Flux.range(1, 5);

        StepVerifier.create(integerFlux.log())
                    .expectNext(1, 2, 3, 4, 5)
                    .verifyComplete();
    }
}
