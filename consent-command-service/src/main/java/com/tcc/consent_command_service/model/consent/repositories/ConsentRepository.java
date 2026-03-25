package com.tcc.consent_command_service.model.consent.repositories;

import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.events.DomainEvent;

import java.util.List;

public interface ConsentRepository {

    void saveEvents(List<DomainEvent> events);

    Consent findByOwnerId(Long ownerId);
}
