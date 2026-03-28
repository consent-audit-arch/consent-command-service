package com.tcc.consent_command_service.unit.application.service;

import com.tcc.consent_command_service.application.services.ConsentService;
import com.tcc.consent_command_service.fixtures.requests.GrantConsentRequestFixture;
import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.events.DomainEventPublisher;
import com.tcc.consent_command_service.model.consent.repositories.ConsentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

    @Mock
    private ConsentRepository consentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ConsentService consentService;

    @Test
    void shouldCreateNewConsentWhenOwnerHasNone() {
        when(consentRepository.findByOwnerId(100L)).thenReturn(null);

        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        verify(consentRepository).saveEvents(anyList());
        verify(eventPublisher).publishAll(anyList());
    }

    @Test
    void shouldReuseExistingConsentWhenOwnerAlreadyHasOne() {
        Consent existing = Consent.builder()
                .consentId(UUID.randomUUID().toString())
                .ownerId(100L)
                .authorizations(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .domainEvents(new ArrayList<>())
                .build();

        when(consentRepository.findByOwnerId(100L)).thenReturn(existing);

        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        verify(consentRepository).saveEvents(anyList());
        verify(eventPublisher).publishAll(anyList());
    }
}