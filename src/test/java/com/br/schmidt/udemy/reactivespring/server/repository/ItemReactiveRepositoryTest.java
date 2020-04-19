package com.br.schmidt.udemy.reactivespring.server.repository;

import com.br.schmidt.udemy.reactivespring.server.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@DirtiesContext
class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 147.99)
    );

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                              .block();

        itemReactiveRepository.saveAll(itemList)
                              .doOnNext(item -> System.out.println("Inserted Item is: " + item))
                              .blockLast();
    }

    @Test
    void getAllItems() {
        final Flux<Item> allItems = itemReactiveRepository.findAll();

        StepVerifier.create(allItems)
                    .expectSubscription()
                    .expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    void getItemByID() {
        final Mono<Item> item = itemReactiveRepository.findById("ABC");

        StepVerifier.create(item)
                    .expectSubscription()
                    .expectNextMatches(item1 -> item1.getDescription().equals("Bose Headphones"))
                    .verifyComplete();
    }

    @Test
    void getItemByDescription() {
        final Flux<Item> item = itemReactiveRepository.findByDescription("Bose Headphones").log();

        StepVerifier.create(item)
                    .expectSubscription()
                    .expectNextMatches(item1 -> item1.getDescription().equals("Bose Headphones"))
                    .verifyComplete();
    }

    @Test
    void saveItem() {
        final Item item = new Item(null, "Google Home Mini", 30.0);

        final Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem)
                    .expectSubscription()
                    .expectNextMatches(item1 -> item1.getId() != null
                            && item1.getDescription().equals("Google Home Mini"))
                    .verifyComplete();
    }

    @Test
    void updateItem() {
        final Flux<Item> updatedItem
                = itemReactiveRepository.findByDescription("LG TV")
                                        .map(item -> {
                                            item.setPrice(520.0);
                                            return item;
                                        })
                                        .flatMap(itemReactiveRepository::save);

        StepVerifier.create(updatedItem)
                    .expectSubscription()
                    .expectNextMatches(item -> item.getPrice() == 520.0)
                    .verifyComplete();
    }

    @Test
    void deleteItemById() {
        final Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                                                             .map(Item::getId)
                                                             .flatMap(itemReactiveRepository::deleteById);

        StepVerifier.create(deletedItem.log())
                    .expectSubscription()
                    .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }

    @Test
    void deleteItemByDescription() {
        final Flux<Void> deletedItem =
                itemReactiveRepository.findByDescription("Bose Headphones")
                                      .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(deletedItem.log())
                    .expectSubscription()
                    .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }
}
