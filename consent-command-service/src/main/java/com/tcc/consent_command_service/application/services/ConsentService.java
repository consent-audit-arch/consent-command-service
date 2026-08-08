package com.tcc.consent_command_service.application.services;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.ConsentAuthorizationRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.DeactivateConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.RevokeConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.responses.DeactivateConsentResponse;
import com.tcc.consent_command_service.infrastructure.client.UserServiceClient;
import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentProjectionJpaEntity;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.ConsentProjectionJpaRepository;
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
    private final UserServiceClient userServiceClient;
    private final ConsentProjectionJpaRepository projectionRepository;

    @Transactional
    public void grantConsent(GrantConsentRequest request) {
        if (!userServiceClient.userExists(request.getOwnerId())) {
            throw new IllegalArgumentException(
                    "User " + request.getOwnerId() + " does not exist. Consent can only be registered for existing users.");
        }

        checkUserNotDeactivated(request.getOwnerId());

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
        if (!userServiceClient.userExists(request.getOwnerId())) {
            throw new IllegalArgumentException(
                    "User " + request.getOwnerId() + " does not exist. Consent can only be managed for existing users.");
        }

        checkUserNotDeactivated(request.getOwnerId());

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

    @Transactional
    public DeactivateConsentResponse deactivateConsent(DeactivateConsentRequest request) {
        Consent consent = consentRepository.findByOwnerId(request.getOwnerId());
        if (consent == null) {
            throw new IllegalArgumentException(
                    "No consent records found for user " + request.getOwnerId());
        }

        if (!hasActiveAuthorizations(consent)) {
            throw new IllegalStateException(
                    "User " + request.getOwnerId() + " has already been deactivated.");
        }

        Issuer systemIssuer = Issuer.builder()
                .id(request.getIssuedBy().getId())
                .issuer(IssuerType.valueOf(request.getIssuedBy().getIssuer()))
                .build();

        int revokedCount = consent.deactivateUser("USER_ACCOUNT_DEACTIVATED", systemIssuer);

        List<DomainEvent> events = consent.getDomainEvents();
        consentRepository.saveEvents(events, consent.getVersion());
        eventPublisher.publishAll(events);
        consent.clearNewDomainEvents();

        log.info("Deactivated user {}: {} consents revoked", request.getOwnerId(), revokedCount);

        return DeactivateConsentResponse.builder()
                .ownerId(request.getOwnerId())
                .consentsRevoked(revokedCount)
                .build();
    }

    private boolean hasActiveAuthorizations(Consent consent) {
        return consent.getAuthorizations() != null && !consent.getAuthorizations().isEmpty();
    }

    private void checkUserNotDeactivated(Long ownerId) {
        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(ownerId);
        if (!projections.isEmpty()) {
            boolean deactivated = projections.stream()
                    .anyMatch(p -> Boolean.FALSE.equals(p.getUserActive()));
            if (deactivated) {
                throw new IllegalStateException("User account is deactivated");
            }
        }
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
                        category, purpose, request.getReason(),
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
                        category, purpose, request.getLegalBasis(),
                        Issuer.builder()
                                .id(request.getIssuedBy().getId())
                                .issuer(IssuerType.valueOf(request.getIssuedBy().getIssuer()))
                                .build()
                );
            }
        }
    }
}
