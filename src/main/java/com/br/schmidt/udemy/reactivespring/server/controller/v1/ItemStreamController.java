package com.br.schmidt.udemy.reactivespring.server.controller.v1;

import com.br.schmidt.udemy.reactivespring.server.document.ItemCapped;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveCappedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.br.schmidt.udemy.reactivespring.server.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemStreamController {

    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemReactiveCappedRepository.findAllBy();
    }

}
