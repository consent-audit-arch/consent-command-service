package com.tcc.consent_command_service.model.consent.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserDeactivatedEvent extends DomainEvent {

    private final String consentId;
    private final String eventType;
    private final Long ownerId;
    private final String reason;
    private final Issuer issuedBy;
    private final LocalDateTime occurredAt;

    @JsonCreator
    public UserDeactivatedEvent(
            @JsonProperty("consentId")  String consentId,
            @JsonProperty("eventType")  String eventType,
            @JsonProperty("ownerId")    Long ownerId,
            @JsonProperty("reason")     String reason,
            @JsonProperty("issuedBy")   Issuer issuedBy,
            @JsonProperty("occurredAt") LocalDateTime occurredAt) {
        this.consentId  = consentId;
        this.eventType  = eventType != null ? eventType : "UserDeactivated";
        this.ownerId    = ownerId;
        this.reason     = reason;
        this.issuedBy   = issuedBy;
        this.occurredAt = occurredAt;
    }
}
