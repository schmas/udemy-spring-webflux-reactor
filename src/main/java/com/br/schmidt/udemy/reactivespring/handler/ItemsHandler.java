package com.br.schmidt.udemy.reactivespring.handler;

import com.br.schmidt.udemy.reactivespring.document.Item;
import com.br.schmidt.udemy.reactivespring.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemsHandler {

    private final ItemReactiveRepository repository;

    static Mono<ServerResponse> notFount = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(final ServerRequest serverRequest) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(repository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(final ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final Mono<Item> itemMono = repository.findById(id);
        return itemMono.flatMap(item -> ServerResponse.ok()
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .bodyValue(item))
                       .switchIfEmpty(notFount);
    }
}
