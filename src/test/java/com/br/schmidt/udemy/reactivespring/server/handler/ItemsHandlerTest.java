package com.br.schmidt.udemy.reactivespring.server.handler;

import com.br.schmidt.udemy.reactivespring.server.constants.ItemConstants;
import com.br.schmidt.udemy.reactivespring.server.document.Item;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
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

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@Profile("test")
class ItemsHandlerTest {

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
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Item.class)
                     .hasSize(4);
    }

    @Test
    void getAllItems_approach2() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Item.class)
                     .hasSize(4)
                     .consumeWith(response -> {
                         final List<Item> items = response.getResponseBody();
                         assert items != null;
                         items.forEach(item -> {
                             Assertions.assertNotNull(item.getId());
                         });

                     });
    }

    @Test
    void getAllItems_approach3() {
        final Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                                                 .exchange()
                                                 .expectStatus().isOk()
                                                 .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                                 .returnResult(Item.class)
                                                 .getResponseBody();

        StepVerifier.create(itemFlux)
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }

    @Test
    void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "ABC")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.price", 149.99);
    }

    @Test
    void getOneItem_notFound() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "DEF")
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        final Item item = new Item(null, "Iphone X", 999.99);

        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectBody()
                     .jsonPath("$.id").isNotEmpty()
                     .jsonPath("$.description").isEqualTo("Iphone X")
                     .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    void deleteItem() {
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "ABC")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        final Item item = new Item(null, "Beats Headphones", 129.99);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "ABC")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.id").isEqualTo("ABC")
                     .jsonPath("$.description").isEqualTo("Beats Headphones")
                     .jsonPath("$.price").isEqualTo(129.99);
    }

    @Test
    void updateItem_notFound() {
        final Item item = new Item(null, "Beats Headphones", 129.99);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "DEF")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void runtimeException() {
        webTestClient.get()
                     .uri("/fun/runtimeexception")
                     .exchange()
                     .expectStatus().is5xxServerError()
                     .expectBody()
                     .jsonPath("$.message", "My Error Occurred 2");
    }
}
