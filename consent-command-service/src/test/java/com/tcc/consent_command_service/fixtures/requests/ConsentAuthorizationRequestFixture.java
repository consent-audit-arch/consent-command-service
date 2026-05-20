package com.tcc.consent_command_service.fixtures.requests;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.ConsentAuthorizationRequest;

import java.util.List;

public class ConsentAuthorizationRequestFixture {

    public static ConsentAuthorizationRequest defaultValues() {
        return ConsentAuthorizationRequest.builder()
                .dataCategory("PERSONAL_DATA")
                .purposes(List.of("PROMOTION"))
                .build();
    }

    public static ConsentAuthorizationRequest defaultValuesTwoPurposes() {
        return ConsentAuthorizationRequest.builder()
                .dataCategory("CONTRACT_DATA")
                .purposes(List.of("PROMOTION", "ANALYTICS"))
                .build();
    }
}
