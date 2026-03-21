package com.tcc.consent_command_service.model.consent.enuns;

import java.util.Arrays;

public enum IssuerType {

    USER("USER"),
    OPERATOR("OPERATOR"),
    SYSTEM("SYSTEM");

    private final String description;

    IssuerType(String description) {
        this.description = description;
    }

    public static IssuerType of(String value) {
        return Arrays.stream(values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid IssuerType: " + value));
    }
}
