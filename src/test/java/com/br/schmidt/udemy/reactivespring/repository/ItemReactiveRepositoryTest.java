package com.br.schmidt.udemy.reactivespring.repository;

import com.br.schmidt.udemy.reactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;
    private List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99)
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
        final Flux<Item> allItems = itemReactiveRepository.findAll().log();

        StepVerifier.create(allItems)
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }
}