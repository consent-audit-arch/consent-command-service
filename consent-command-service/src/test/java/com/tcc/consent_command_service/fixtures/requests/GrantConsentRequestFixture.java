package com.tcc.consent_command_service.fixtures.requests;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;

import java.time.LocalDateTime;
import java.util.List;

public class GrantConsentRequestFixture {

    public static GrantConsentRequest defaultValues() {
        return GrantConsentRequest.builder()
                .ownerId(100L)
                .authorizations(
                        List.of(
                                ConsentAuthorizationRequestFixture.defaultValues(),
                                ConsentAuthorizationRequestFixture.defaultValuesTwoPurposes()
                        )
                )
                .occurredAt(LocalDateTime.of(2025,5,25,5,0))
                .legalBasis("CONSENT")
                .issuedBy(IssuerRequestFixture.defaultValues())
                .build();
    }

}
