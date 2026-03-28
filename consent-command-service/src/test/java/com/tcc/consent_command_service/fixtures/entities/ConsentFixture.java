package com.tcc.consent_command_service.fixtures.entities;

import com.tcc.consent_command_service.fixtures.valueObjects.ConsentAuthorizationFixture;
import com.tcc.consent_command_service.model.consent.entities.Consent;

import java.time.LocalDateTime;
import java.util.Set;

public class ConsentFixture {

    public static Consent defaultValues() {
        return Consent.builder()
                .ownerId(100L)
                .authorizations(
                        Set.of(
                                ConsentAuthorizationFixture.defaultValues(),
                                ConsentAuthorizationFixture.defaultValuesTwoPurposes()
                        )
                )
                .occurredAt(LocalDateTime.of(2025,5,25,5,0))
                .build();
    }

}
