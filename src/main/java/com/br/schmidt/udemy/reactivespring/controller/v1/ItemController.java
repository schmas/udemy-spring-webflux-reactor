package com.br.schmidt.udemy.reactivespring.controller.v1;

import com.br.schmidt.udemy.reactivespring.constants.ItemConstants;
import com.br.schmidt.udemy.reactivespring.document.Item;
import com.br.schmidt.udemy.reactivespring.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

}
