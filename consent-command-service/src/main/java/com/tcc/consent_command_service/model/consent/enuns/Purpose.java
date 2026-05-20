package com.tcc.consent_command_service.model.consent.enuns;

import java.util.Arrays;

public enum Purpose {
    PROMOTION("PROMOTION"),
    BILLING("BILLING"),
    ANALYTICS("ANALYTICS"),
    CUSTOMER_SERVICE("CUSTOMER_SERVICE");

    private final String description;

    Purpose(String description) {
        this.description = description;
    }

    public static Purpose of(String value) {
        return Arrays.stream(values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Purpose: " + value));
    }
}
