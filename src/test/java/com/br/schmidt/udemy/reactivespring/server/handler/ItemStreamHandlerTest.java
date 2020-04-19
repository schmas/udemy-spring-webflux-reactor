package com.br.schmidt.udemy.reactivespring.server.handler;

import com.br.schmidt.udemy.reactivespring.server.document.ItemCapped;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveCappedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.br.schmidt.udemy.reactivespring.server.constants.ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@Profile("test")
public class ItemStreamHandlerTest {

    @Autowired
    private ItemReactiveCappedRepository repository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class,
                                         CollectionOptions.empty()
                                                          .maxDocuments(20)
                                                          .size(50000)
                                                          .capped());

        final Flux<ItemCapped> itemCappedFlux =
                Flux.interval(Duration.ofMillis(100))
                    .map(i -> new ItemCapped(null, "Random Item " + i, (100.00 + i)))
                    .take(5);
        repository
                .insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted item is: " + itemCapped);
                })
                .blockLast();
    }

    @Test
    void testStreamAllItems() {
        final Flux<ItemCapped> itemCappedFlux =
                webTestClient.get()
                             .uri(ITEM_STREAM_FUNCTIONAL_END_POINT_V1)
                             .exchange()
                             .expectStatus().isOk()
                             .returnResult(ItemCapped.class)
                             .getResponseBody()
                             .take(5);

        StepVerifier.create(itemCappedFlux)
                    .expectSubscription()
                    .expectNextCount(5)
                    .thenCancel()
                    .verify();
    }

}
