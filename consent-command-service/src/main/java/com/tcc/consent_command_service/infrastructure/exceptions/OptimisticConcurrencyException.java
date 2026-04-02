package com.tcc.consent_command_service.infrastructure.exceptions;

public class OptimisticConcurrencyException extends RuntimeException {
    public OptimisticConcurrencyException(String message) {
        super(message);
    }
}
