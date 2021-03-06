package com.br.schmidt.udemy.reactivespring.server.controller.v1;

import com.br.schmidt.udemy.reactivespring.server.constants.ItemConstants;
import com.br.schmidt.udemy.reactivespring.server.document.Item;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ItemConstants.ITEM_END_POINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable final String id) {
        return itemReactiveRepository.findById(id)
                                     .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                                     .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ItemConstants.ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody final Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteItem(@PathVariable final String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ItemConstants.ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable final String id,
                                                 @RequestBody final Item item) {
        return itemReactiveRepository.findById(id)
                                     .flatMap(currItem -> {
                                         currItem.setPrice(item.getPrice());
                                         currItem.setDescription(item.getDescription());
                                         return itemReactiveRepository.save(currItem);
                                     })
                                     .map(ResponseEntity::ok)
                                     .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(ItemConstants.ITEM_END_POINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException() {
        return itemReactiveRepository.findAll()
                                     .concatWith(Mono.error(new RuntimeException("My Error Occurred")));
    }


}
