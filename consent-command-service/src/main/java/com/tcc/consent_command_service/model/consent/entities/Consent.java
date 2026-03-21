package com.tcc.consent_command_service.model.consent.entities;

import com.tcc.consent_command_service.model.consent.valueObjects.ConsentAuthorization;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Consent {

    private String consentId;
    private Long ownerId;
    private List<ConsentAuthorization> authorizations;
    private String legalBasis;
    private Issuer issuedBy;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;

}
