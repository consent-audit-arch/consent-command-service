package com.tcc.consent_command_service.model.consent.enuns;

import java.util.Arrays;

public enum DataCategory {
    PERSONAL_DATA("PERSONAL_DATA"),
    CONTRACT_DATA("CONTRACT_DATA"),
    FINANCIAL_DATA("FINANCIAL_DATA"),
    USAGE_DATA("USAGE_DATA");

    private final String description;

    DataCategory(String description) {
        this.description = description;
    }

    public static DataCategory of(String value) {
        return Arrays.stream(values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid DataCategory: " + value));
    }
}
