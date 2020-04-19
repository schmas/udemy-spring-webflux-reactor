package com.br.schmidt.udemy.reactivespring.server.initialize;

import com.br.schmidt.udemy.reactivespring.server.document.Item;
import com.br.schmidt.udemy.reactivespring.server.document.ItemCapped;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveCappedRepository;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemReactiveRepository;
    private final ItemReactiveCappedRepository itemReactiveCappedRepository;
    private final MongoOperations mongoOperations;

    @Override
    public void run(final String... args) throws Exception {
        initialDataSetUp();
        createCappedCollections();
        dataSetUpForCappedCollection();
    }

    public List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats Headphones", 149.99)
        );

    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()
                              .thenMany(Flux.fromIterable(data()))
                              .flatMap(itemReactiveRepository::save)
                              .thenMany(itemReactiveRepository.findAll())
                              .subscribe(item -> System.out.println("Item inserted from CommandLineRunner : " + item));
    }

    private void createCappedCollections() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty()
                                                                            .capped()
                                                                            .maxDocuments(20)
                                                                            .size(50000));


    }

    public void dataSetUpForCappedCollection() {
        final Flux<ItemCapped> itemCappedFlux =
                Flux.interval(Duration.ofSeconds(1))
                    .map(i -> new ItemCapped(null, "Random Item " + i, (100.00 + i)));
        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> {
                    log.info("Inserted item is: {}", itemCapped);
                });
    }
}
