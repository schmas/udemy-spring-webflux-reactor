package com.br.schmidt.udemy.reactivespring.router;

import com.br.schmidt.udemy.reactivespring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.br.schmidt.udemy.reactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(final ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)),
                       itemsHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                          itemsHandler::getOneItem)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)),
                          itemsHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                          itemsHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                          itemsHandler::updateItem);
    }

}
