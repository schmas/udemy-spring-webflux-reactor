package com.br.schmidt.udemy.reactivespring.controller.v1;

import com.br.schmidt.udemy.reactivespring.constants.ItemConstants;
import com.br.schmidt.udemy.reactivespring.document.Item;
import com.br.schmidt.udemy.reactivespring.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ItemConstants.ITEM_END_POINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

}
