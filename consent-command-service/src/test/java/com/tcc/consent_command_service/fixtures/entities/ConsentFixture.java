package com.tcc.consent_command_service.fixtures.entities;

import com.tcc.consent_command_service.fixtures.valueObjects.ConsentAuthorizationFixture;
import com.tcc.consent_command_service.fixtures.valueObjects.IssuerFixture;
import com.tcc.consent_command_service.model.consent.entities.Consent;

import java.time.LocalDateTime;
import java.util.List;

public class ConsentFixture {

    public static Consent defaultValues() {
        return Consent.builder()
                .ownerId(100L)
                .authorizations(
                        List.of(
                                ConsentAuthorizationFixture.defaultValues(),
                                ConsentAuthorizationFixture.defaultValuesTwoPurposes()
                        )
                )
                .occurredAt(LocalDateTime.of(2025,5,25,5,0))
                .legalBasis("CONSENT")
                .issuedBy(IssuerFixture.defaultValues())
                .build();
    }

}
