package com.banquito.sistema.originacion.exception;

public class InvalidStateException extends RuntimeException {

    private final String currentState;
    private final String targetState;
    private final String entity;

    public InvalidStateException(String currentState, String targetState, String entity) {
        super();
        this.currentState = currentState;
        this.targetState = targetState;
        this.entity = entity;
    }

    @Override
    public String getMessage() {
        return "No se puede cambiar el estado de " + this.entity + " de " + currentState + " a " + targetState;
    }
} 