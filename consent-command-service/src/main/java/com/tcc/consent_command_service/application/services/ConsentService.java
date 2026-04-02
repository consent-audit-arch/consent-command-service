package com.tcc.consent_command_service.application.services;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.ConsentAuthorizationRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.RevokeConsentRequest;
import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.IssuerType;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.events.DomainEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEventPublisher;
import com.tcc.consent_command_service.model.consent.repositories.ConsentRepository;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    private final DomainEventPublisher eventPublisher;


    @Transactional
    public void grantConsent(GrantConsentRequest request) {

        Consent consent = consentRepository.findByOwnerId(request.getOwnerId());

        if (consent == null) {
            consent = createNewConsentFromGrant(request);
        }

        grantConsents(request, consent);

        List<DomainEvent> events = consent.getDomainEvents();

        consentRepository.saveEvents(events, consent.getVersion());

        eventPublisher.publishAll(events);

        consent.clearNewDomainEvents();

    }

    @Transactional
    public void revokeConsent(RevokeConsentRequest request) {

        Consent consent = consentRepository.findByOwnerId(request.getOwnerId());

        if (consent == null) {
            consent = createNewConsentFromRevoke(request);
        }

        revokeConsents(request, consent);

        List<DomainEvent> events = consent.getDomainEvents();

        consentRepository.saveEvents(events, consent.getVersion());

        eventPublisher.publishAll(events);

        consent.clearNewDomainEvents();

    }

    private Consent createNewConsentFromGrant(GrantConsentRequest request) {
        return Consent.builder()
                .consentId(UUID.randomUUID().toString())
                .version(0L)
                .ownerId(request.getOwnerId())
                .authorizations(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .domainEvents(new ArrayList<>())
                .build();
    }

    private Consent createNewConsentFromRevoke(RevokeConsentRequest request) {
        return Consent.builder()
                .consentId(UUID.randomUUID().toString())
                .version(0L)
                .ownerId(request.getOwnerId())
                .authorizations(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .domainEvents(new ArrayList<>())
                .build();
    }

    private void revokeConsents(RevokeConsentRequest request, Consent consent) {
        for (ConsentAuthorizationRequest authorization : request.getAuthorizations()) {
            DataCategory category = DataCategory.of(authorization.getDataCategory());

            for (String purposeRequest : authorization.getPurposes()) {
                Purpose purpose = Purpose.of(purposeRequest);

                consent.revokeAuthorization(
                        category,
                        purpose,
                        request.getReason(),
                        Issuer.builder()
                                .id(request.getIssuedBy().getId())
                                .issuer(IssuerType.valueOf(request.getIssuedBy().getIssuer()))
                                .build()
                );
            }
        }
    }

    private void grantConsents(GrantConsentRequest request, Consent consent) {
        for (ConsentAuthorizationRequest authorization : request.getAuthorizations()) {
            DataCategory category = DataCategory.of(authorization.getDataCategory());

            for (String purposeRequest : authorization.getPurposes()) {
                Purpose purpose = Purpose.of(purposeRequest);

                consent.grantAuthorization(
                        category,
                        purpose,
                        request.getLegalBasis(),
                        Issuer.builder()
                                .id(request.getIssuedBy().getId())
                                .issuer(IssuerType.valueOf(request.getIssuedBy().getIssuer()))
                                .build()
                );
            }
        }
    }

}
