package com.br.schmidt.udemy.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest
class FluxAndMonoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void flux_approach1() {
        final Flux<Integer> integerFlux =
                webTestClient.get().uri("/flux")
                             .accept(MediaType.APPLICATION_JSON)
                             .exchange()
                             .expectStatus().isOk()
                             .returnResult(Integer.class)
                             .getResponseBody();

        StepVerifier.create(integerFlux)
                    .expectSubscription()
                    .expectNext(1, 2, 3, 4)
                    .verifyComplete();
    }

    @Test
    void flux_approach2() {
        webTestClient.get().uri("/flux")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Integer.class)
                     .hasSize(4);
    }

    @Test
    void flux_approach3() {
        final List<Integer> expectedIntegers = Arrays.asList(1, 2, 3, 4);

        final EntityExchangeResult<List<Integer>> entityExchangeResult =
                webTestClient.get().uri("/flux")
                             .accept(MediaType.APPLICATION_JSON)
                             .exchange()
                             .expectStatus().isOk()
                             .expectBodyList(Integer.class)
                             .returnResult();

        assertEquals(expectedIntegers, entityExchangeResult.getResponseBody());
    }

    @Test
    void flux_approach4() {
        final List<Integer> expectedIntegers = Arrays.asList(1, 2, 3, 4);

        webTestClient.get().uri("/flux")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBodyList(Integer.class)
                     .consumeWith(response -> {
                         assertEquals(expectedIntegers, response.getResponseBody());
                     });
    }

    @Test
    void fluxStream() {
        final Flux<Long> longFlux =
                webTestClient.get().uri("/fluxstream")
                             .accept(MediaType.APPLICATION_STREAM_JSON)
                             .exchange()
                             .expectStatus().isOk()
                             .returnResult(Long.class)
                             .getResponseBody();

        StepVerifier.create(longFlux)
                    .expectSubscription()
                    .expectNext(0L)
                    .expectNext(1L)
                    .expectNext(2L)
                    .thenCancel()
                    .verify();
    }

    @Test
    void mono() {
        final int expected = 1;

        webTestClient.get().uri("/mono")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange().expectStatus().isOk()
                     .expectBody(Integer.class)
                     .consumeWith(integerEntityExchangeResult -> {
                         assertEquals(expected, integerEntityExchangeResult.getResponseBody());
                     });
    }
}
