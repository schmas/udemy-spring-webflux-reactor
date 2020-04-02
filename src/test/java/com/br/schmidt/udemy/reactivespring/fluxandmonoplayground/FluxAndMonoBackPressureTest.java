package com.br.schmidt.udemy.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    void backPressureTest() {

        final Flux<Integer> finiteFlux =
                Flux.range(1, 10)
                    .log();

        StepVerifier.create(finiteFlux)
                    .expectSubscription()
                    .thenRequest(1)
                    .expectNext(1)
                    .thenRequest(1)
                    .expectNext(2)
                    .thenCancel()
                    .verify();
    }

    @Test
    void backPressure() {
        final Flux<Integer> finiteFlux =
                Flux.range(1, 10)
                    .log();

        finiteFlux.subscribe(e -> System.out.println("Element is : " + e)
                , err -> System.err.println("Exception is " + err)
                , () -> System.out.println("Done")
                , subscription -> subscription.request(2));
    }

    @Test
    void backPressure_cancel() {
        final Flux<Integer> finiteFlux =
                Flux.range(1, 10)
                    .log();

        finiteFlux.subscribe(e -> System.out.println("Element is : " + e)
                , err -> System.err.println("Exception is " + err)
                , () -> System.out.println("Done")
                , Subscription::cancel);
    }

    @Test
    void customized_backPressure() {
        final Flux<Integer> finiteFlux =
                Flux.range(1, 10)
                    .log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(final Integer value) {
                request(1);
                System.out.println("Value received is : " + value);

                if (value == 4) {
                    cancel();
                }
            }
        });
    }

}
