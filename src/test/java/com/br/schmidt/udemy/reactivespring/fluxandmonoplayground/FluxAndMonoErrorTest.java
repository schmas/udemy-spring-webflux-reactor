package com.br.schmidt.udemy.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    void fluxErrorHandling() {
        final Flux<String> stringFlux =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .concatWith(Flux.just("D"))
                    .onErrorResume(e -> {
                        System.out.println("Exception is : " + e);
                        return Flux.just("default", "default1");
                    })
                    .log();

        StepVerifier.create(stringFlux)
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    // .expectError(RuntimeException.class)
                    // .verify();
                    .expectNext("default", "default1")
                    .verifyComplete();
    }

    @Test
    void fluxErrorHandling_onErrorReturn() {
        final Flux<String> stringFlux =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .concatWith(Flux.just("D"))
                    .onErrorReturn("default")
                    .log();

        StepVerifier.create(stringFlux)
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("default")
                    .verifyComplete();
    }

    @Test
    void fluxErrorHandling_onErrorMap() {
        final Flux<String> stringFlux =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .concatWith(Flux.just("D"))
                    .onErrorMap(CustomException::new)
                    .log();

        StepVerifier.create(stringFlux)
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectError(CustomException.class)
                    .verify();
    }

    @Test
    void fluxErrorHandling_onErrorMap_withRetry() {
        final Flux<String> stringFlux =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .concatWith(Flux.just("D"))
                    .onErrorMap(CustomException::new)
                    .retry(2);

        StepVerifier.create(stringFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectError(CustomException.class)
                    .verify();
    }

    @Test
    void fluxErrorHandling_onErrorMap_withRetryBackoff() {
        final Flux<String> stringFlux =
                Flux.just("A", "B", "C")
                    .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                    .concatWith(Flux.just("D"))
                    .onErrorMap(CustomException::new)
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)));

        StepVerifier.create(stringFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectError(IllegalStateException.class)
                    .verify();
    }
}
