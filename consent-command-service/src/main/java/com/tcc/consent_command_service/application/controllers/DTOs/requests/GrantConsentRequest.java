package com.tcc.consent_command_service.application.controllers.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GrantConsentRequest {
    @NotBlank
    private Long ownerId;

    @NotNull
    @Size(min = 1)
    private List<ConsentAuthorizationRequest> authorizations;

    @NotBlank
    private String legalBasis;

    private LocalDateTime occurredAt;

    @NotNull
    private IssuerRequest issuedBy;
}
