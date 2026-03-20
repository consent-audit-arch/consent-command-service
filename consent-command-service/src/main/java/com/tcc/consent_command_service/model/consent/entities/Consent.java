package com.tcc.consent_command_service.model.consent.entities;

import com.tcc.consent_command_service.model.consent.valueObjects.valueObjects.ConsentAuthorization;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Consent {

    private String consentId;
    private String ownerId;
    private List<ConsentAuthorization> authorizations;
    private String legalBasis;
    private LocalDateTime expiresAt;
    private String issuedBy;
    private LocalDateTime createdAt;

}
