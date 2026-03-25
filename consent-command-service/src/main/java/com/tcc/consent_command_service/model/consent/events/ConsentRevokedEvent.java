package com.tcc.consent_command_service.model.consent.events;

import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ConsentRevokedEvent extends DomainEvent {
    String consentId;
    Long ownerId;
    DataCategory dataCategory;
    Purpose purpose;
    String reason;
    Issuer issuedBy;
    LocalDateTime occurredAt;
}
