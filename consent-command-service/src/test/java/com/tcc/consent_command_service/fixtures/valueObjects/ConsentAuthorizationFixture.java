package com.tcc.consent_command_service.fixtures.valueObjects;

import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.valueObjects.ConsentAuthorization;

import java.util.Set;

public class ConsentAuthorizationFixture {

    public static ConsentAuthorization defaultValues() {
        return ConsentAuthorization.builder()
                .dataCategory(DataCategory.PERSONAL_DATA)
                .purposes(Set.of(Purpose.PROMOTION))
                .build();
    }

    public static ConsentAuthorization defaultValuesTwoPurposes() {
        return ConsentAuthorization.builder()
                .dataCategory(DataCategory.CONTRACT_DATA)
                .purposes(Set.of(Purpose.PROMOTION, Purpose.ANALYTICS))
                .build();
    }

}
