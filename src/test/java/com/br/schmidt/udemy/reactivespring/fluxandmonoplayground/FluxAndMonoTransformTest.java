package com.br.schmidt.udemy.reactivespring.fluxandmonoplayground;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

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

    @Test
    void transformUsingFlatMap() {

        final Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                                            .flatMap(s -> {
                                                return Flux.fromIterable(convertToList(s));
                                            }); //db or external service call that returns a flux -> s -> Flux<String>

        StepVerifier.create(stringFlux.log())
                    .expectNextCount(12)
                    .verifyComplete();

    }

    @SneakyThrows
    private List<String> convertToList(final String s) {
        Thread.sleep(1000);
        return Arrays.asList(s, "newValue");
    }

    @Test
    void transformUsingFlatMap_UsingParallel() {

        final Flux<String> stringFlux =
                Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                    .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                    .flatMap(s -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>
                    .flatMap(Flux::fromIterable) // Flux<String>
                    .log();

        StepVerifier.create(stringFlux)
                    .expectNextCount(12)
                    .verifyComplete();

    }

    @Test
    void transformUsingFlatMap_parallel_maintain_order() {

        final Flux<String> stringFlux =
                Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                    .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                    // .concatMap(s -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>
                    .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>
                    .flatMap(Flux::fromIterable) // Flux<String>
                    .log();

        StepVerifier.create(stringFlux)
                    .expectNextCount(12)
                    .verifyComplete();

    }
}
