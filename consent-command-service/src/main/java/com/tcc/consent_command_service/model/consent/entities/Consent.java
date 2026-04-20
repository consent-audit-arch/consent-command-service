package com.tcc.consent_command_service.model.consent.entities;

import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.events.ConsentGrantedEvent;
import com.tcc.consent_command_service.model.consent.events.ConsentRevokedEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEvent;
import com.tcc.consent_command_service.model.consent.valueObjects.ConsentAuthorization;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;


@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Consent {

    private String consentId;
    private Long ownerId;
    private Set<ConsentAuthorization> authorizations;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private Long version;

    private List<DomainEvent> domainEvents = new ArrayList<>();

    public void grantAuthorization(
            DataCategory dataCategory,
            Purpose purpose,
            String legalBasis,
            Issuer issuedBy) {

        Optional<ConsentAuthorization> existing = this.authorizations.stream()
                .filter(auth -> auth.dataCategory().equals(dataCategory))
                .findFirst();

        if (existing.isPresent()) {
            Set<Purpose> updatedPurposes = new HashSet<>(existing.get().purposes());
            updatedPurposes.add(purpose);
            this.authorizations.remove(existing.get());
            this.authorizations.add(ConsentAuthorization.of(dataCategory, updatedPurposes));
        } else {
            this.authorizations.add(ConsentAuthorization.of(dataCategory, Set.of(purpose)));
        }

        this.domainEvents.add(new ConsentGrantedEvent(
                this.consentId, "ConsentGranted", this.ownerId, dataCategory, purpose,
                legalBasis, issuedBy, LocalDateTime.now()
        ));
    }

    public void revokeAuthorization(
            DataCategory dataCategory,
            Purpose purpose,
            String reason,
            Issuer issuedBy) {

        Optional<ConsentAuthorization> existing = this.authorizations.stream()
                .filter(auth -> auth.dataCategory().equals(dataCategory))
                .findFirst();

        if (existing.isPresent()) {
            Set<Purpose> updatedPurposes = new HashSet<>(existing.get().purposes());
            updatedPurposes.remove(purpose);
            this.authorizations.remove(existing.get());

            if (!updatedPurposes.isEmpty()) {
                this.authorizations.add(ConsentAuthorization.of(dataCategory, updatedPurposes));
            }
        }

        this.domainEvents.add(new ConsentRevokedEvent(
                this.consentId, "ConsentRevoked", this.ownerId, dataCategory, purpose,
                reason, issuedBy, LocalDateTime.now()
        ));
    }

    public void clearNewDomainEvents() {
        this.domainEvents.clear();
    }
}
