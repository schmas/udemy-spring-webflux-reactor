package com.br.schmidt.udemy.reactivespring.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(final RuntimeException exception) {
        log.error("Exception caught in handleRuntimeException", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(final Exception exception) {
        log.error("Exception caught in handleException", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(exception.getMessage());
    }

}
