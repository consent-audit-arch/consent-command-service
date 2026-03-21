package com.tcc.consent_command_service.fixtures.valueObjects;

import com.tcc.consent_command_service.model.consent.enuns.IssuerType;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;

public class IssuerFixture {

    public static Issuer defaultValues() {
        return Issuer.builder()
                .issuer(IssuerType.USER)
                .id(100L)
                .build();
    }

}
