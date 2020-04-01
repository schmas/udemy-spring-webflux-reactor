package com.br.schmidt.udemy.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    private List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    void filterTest() {
        final Flux<String> namesFlux
                = Flux.fromIterable(names)
                      .filter(s -> s.startsWith("a"));

        StepVerifier.create(namesFlux.log())
                    .expectNext("adam", "anna")
                    .verifyComplete();
    }

    @Test
    void filterTestLength() {
        final Flux<String> namesFlux
                = Flux.fromIterable(names)
                      .filter(s -> s.length() > 4);

        StepVerifier.create(namesFlux.log())
                    .expectNext("jenny")
                    .verifyComplete();
    }
}
