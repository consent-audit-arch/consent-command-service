package com.tcc.consent_command_service.model.consent.valueObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc.consent_command_service.model.consent.enuns.IssuerType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Issuer {

    @JsonProperty("issuer") IssuerType issuer;
    @JsonProperty("id")     Long id;

    @JsonCreator
    public Issuer(@JsonProperty("issuer") IssuerType issuer,
                  @JsonProperty("id") Long id) {
        if (issuer == null) {
            throw new IllegalArgumentException("IssuerType não pode ser nulo.");
        }

        if (id == null) {
            throw new IllegalArgumentException("IssuerType não pode ser nulo.");
        }

        this.issuer = issuer;
        this.id = id;
    }

    public static Issuer of(IssuerType issuer, Long id) {
        return new Issuer(issuer, id);
    }
}
