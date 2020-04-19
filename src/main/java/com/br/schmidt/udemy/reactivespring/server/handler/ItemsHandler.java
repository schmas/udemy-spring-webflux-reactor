package com.br.schmidt.udemy.reactivespring.server.handler;

import com.br.schmidt.udemy.reactivespring.server.document.Item;
import com.br.schmidt.udemy.reactivespring.server.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

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

    public Mono<ServerResponse> createItem(final ServerRequest serverRequest) {
        final Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono
                .flatMap(repository::save)
                .flatMap(item -> ServerResponse.created(serverRequest.uri())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .body(Mono.just(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(final ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final Mono<Void> voidMono = repository.deleteById(id);
        return ServerResponse.status(HttpStatus.NO_CONTENT)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(voidMono, Void.class);
    }

    public Mono<ServerResponse> updateItem(final ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(Item.class)
                            .flatMap(item -> repository.findById(id)
                                                       .flatMap(currentItem -> {
                                                           currentItem.setDescription(item.getDescription());
                                                           currentItem.setPrice(item.getPrice());
                                                           return repository.save(currentItem);
                                                       })
                            ).flatMap(item -> ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .body(fromValue(item))
                ).switchIfEmpty(notFount);
    }
}
