package com.tcc.consent_command_service.model.consent.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
public class ConsentRevokedEvent extends DomainEvent {

    private final String consentId;
    private final String eventType;
    private final Long ownerId;
    private final DataCategory dataCategory;
    private final Purpose purpose;
    private final String reason;
    private final Issuer issuedBy;
    private final LocalDateTime occurredAt;

    @JsonCreator
    public ConsentRevokedEvent(
            @JsonProperty("consentId")    String consentId,
            @JsonProperty("eventType")    String eventType,
            @JsonProperty("ownerId")      Long ownerId,
            @JsonProperty("dataCategory") DataCategory dataCategory,
            @JsonProperty("purpose")      Purpose purpose,
            @JsonProperty("reason")       String reason,
            @JsonProperty("issuedBy")     Issuer issuedBy,
            @JsonProperty("occurredAt")   LocalDateTime occurredAt) {
        this.consentId    = consentId;
        this.eventType  = eventType != null ? eventType : "ConsentRevoked";
        this.ownerId      = ownerId;
        this.dataCategory = dataCategory;
        this.purpose      = purpose;
        this.reason       = reason;
        this.issuedBy     = issuedBy;
        this.occurredAt   = occurredAt;
    }
}
