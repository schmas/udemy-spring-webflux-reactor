package com.br.schmidt.udemy.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    private List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    void transformUsingMap() {
        final Flux<String> namesFlux = Flux.fromIterable(names)
                                           .map(String::toUpperCase);

        StepVerifier.create(namesFlux.log())
                    .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                    .verifyComplete();
    }

    @Test
    void transformUsingMap_Length() {
        final Flux<Integer> namesFlux = Flux.fromIterable(names)
                                            .map(String::length);

        StepVerifier.create(namesFlux.log())
                    .expectNext(4, 4, 4, 5)
                    .verifyComplete();
    }

    @Test
    void transformUsingMap_Length_repeat() {
        final Flux<Integer> namesFlux = Flux.fromIterable(names)
                                            .repeat(1)
                                            .map(String::length);

        StepVerifier.create(namesFlux.log())
                    .expectNext(4, 4, 4, 5)
                    .expectNext(4, 4, 4, 5)
                    .verifyComplete();
    }

    @Test
    void transformUsingMap_Length_Filter() {
        final Flux<String> namesFlux = Flux.fromIterable(names)
                                           .filter(s -> s.length() > 4)
                                           .map(String::toUpperCase);

        StepVerifier.create(namesFlux.log())
                    .expectNext("JENNY")
                    .verifyComplete();
    }

}
