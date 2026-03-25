package com.tcc.consent_command_service.model.consent.repositories;

import com.tcc.consent_command_service.model.consent.entities.Consent;

public interface ConsentRepository {

    Long save(Consent consent);

    Consent findByOwnerId(Long ownerId);

}
