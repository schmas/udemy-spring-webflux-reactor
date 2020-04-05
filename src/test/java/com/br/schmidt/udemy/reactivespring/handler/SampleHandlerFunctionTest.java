package com.br.schmidt.udemy.reactivespring.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
class SampleHandlerFunctionTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void flux_approach1() {
        final Flux<Integer> integerFlux =
                webTestClient.get().uri("/functional/flux")
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
    void mono() {
        final int expected = 1;

        webTestClient.get().uri("/functional/mono")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange().expectStatus().isOk()
                     .expectBody(Integer.class)
                     .consumeWith(integerEntityExchangeResult -> {
                         assertEquals(expected, integerEntityExchangeResult.getResponseBody());
                     });
    }
}
