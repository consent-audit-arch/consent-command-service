package com.tcc.consent_command_service.model.consent.enuns;

public enum Purpose {
    PROMOTION("Promoção"),
    BILLING("Cobrança"),
    ANALYTICS("Análise"),
    CUSTOMER_SERVICE("Atendimento");

    private final String description;

    Purpose(String description) {
        this.description = description;
    }
}
