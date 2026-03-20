package com.tcc.consent_command_service.model.consent.enuns;

public enum DataCategory {
    PERSONAL_DATA("Dados Pessoais"),
    CONTRACT_DATA("Dados Contratuais"),
    FINANCIAL_DATA("Dados Financeiros");

    private final String description;

    DataCategory(String description) {
        this.description = description;
    }
}
