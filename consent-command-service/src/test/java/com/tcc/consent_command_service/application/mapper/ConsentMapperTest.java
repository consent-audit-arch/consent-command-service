package com.tcc.consent_command_service.application.mapper;

import com.tcc.consent_command_service.application.mappers.ConsentMapper;
import com.tcc.consent_command_service.fixtures.entities.ConsentFixture;
import com.tcc.consent_command_service.fixtures.requests.GrantConsentRequestFixture;
import com.tcc.consent_command_service.model.consent.entities.Consent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsentMapperTest {

    private final ConsentMapper mapper = Mappers.getMapper(ConsentMapper.class);

    @Test
    void shouldMapRequestToDomain() {
        Consent expected = ConsentFixture.defaultValues();

        Consent result = mapper.grantConsentRequestToDomain(GrantConsentRequestFixture.defaultValues());

        assertEquals(result, expected);

    }

}
