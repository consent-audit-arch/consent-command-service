package com.tcc.consent_command_service.fixtures.requests;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.IssuerRequest;

public class IssuerRequestFixture {

    public static IssuerRequest defaultValues() {
        return IssuerRequest.builder()
                .issuer("USER")
                .id(100L)
                .build();
    }

}
