package com.tcc.consent_command_service.fixtures.requests;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.ConsentAuthorizationRequest;

import java.util.List;

public class ConsentAuthorizationRequestFixture {

    public static ConsentAuthorizationRequest defaultValues() {
        return ConsentAuthorizationRequest.builder()
                .dataCategory("Dados Pessoais")
                .purposes(List.of("Promoção"))
                .build();
    }

    public static ConsentAuthorizationRequest defaultValuesTwoPurposes() {
        return ConsentAuthorizationRequest.builder()
                .dataCategory("Dados Contratuais")
                .purposes(List.of("Promoção", "Análise"))
                .build();
    }
}
