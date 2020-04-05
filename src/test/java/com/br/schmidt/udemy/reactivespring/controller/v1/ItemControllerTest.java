package com.br.schmidt.udemy.reactivespring.controller.v1;

import com.br.schmidt.udemy.reactivespring.constants.ItemConstants;
import com.br.schmidt.udemy.reactivespring.document.Item;
import com.br.schmidt.udemy.reactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@Profile("test")
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> data = Arrays.asList(
            new Item(null, "Samsung TV", 399.99),
            new Item(null, "LG TV", 329.99),
            new Item(null, "Apple Watch", 349.99),
            new Item("ABC", "Beats Headphones", 149.99)
    );

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                              .thenMany(Flux.fromIterable(data))
                              .flatMap(itemReactiveRepository::save)
                              .doOnNext(item -> System.out.println("Inserted Item is: " + item))
                              .blockLast();
    }

    @Test
    void getAllItems() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Item.class)
                     .hasSize(4);
    }

    @Test
    void getAllItems_approach2() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Item.class)
                     .hasSize(4)
                     .consumeWith(entityResult -> {
                         final List<Item> items = entityResult.getResponseBody();
                         Objects.requireNonNull(items).forEach(item -> {
                             assertNotNull(item.getId());
                         });
                     });
    }

    @Test
    void getAllItems_approach3() {
        final Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                                                  .exchange()
                                                  .expectStatus().isOk()
                                                  .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                                  .returnResult(Item.class)
                                                  .getResponseBody();

        StepVerifier.create(itemsFlux.log("value"))
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }

    @Test
    void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1 + "/{id}", "ABC")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.price", 149.99);
    }

    @Test
    void getOneItem_notFound() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1 + "/{id}", "DEF")
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        final Item item = new Item(null, "Iphone X", 999.99);

        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectBody()
                     .jsonPath("$.id").isNotEmpty()
                     .jsonPath("$.description").isEqualTo("Iphone X")
                     .jsonPath("$.price").isEqualTo(999.99);
    }
}
