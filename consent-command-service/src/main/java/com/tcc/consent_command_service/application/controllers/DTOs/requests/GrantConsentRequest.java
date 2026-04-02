package com.tcc.consent_command_service.application.controllers.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantConsentRequest {
    @NotNull
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
