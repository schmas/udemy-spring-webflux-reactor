package com.br.schmidt.udemy.reactivespring.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Slf4j
@Component
public class FunctionalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public FunctionalErrorWebExceptionHandler(final ErrorAttributes errorAttributes,
                                              final ApplicationContext applicationContext,
                                              final ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest serverRequest) {
        final Map<String, Object> errorAttributes = getErrorAttributes(serverRequest, false);
        log.info("errorAttributes: {}", errorAttributes);


        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(fromValue(errorAttributes.entrySet()
                                                            .stream()
                                                            .filter(entry -> entry.getKey().equals("message"))
                                                            .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                      Map.Entry::getValue))
                                   )
                             );
    }
}
