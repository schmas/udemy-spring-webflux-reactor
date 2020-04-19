package com.br.schmidt.udemy.reactivespring.server.fluxandmonoplayground;

public class CustomException extends Throwable {
    public CustomException(final Throwable e) {
        super(e.getMessage(), e);
    }
}
