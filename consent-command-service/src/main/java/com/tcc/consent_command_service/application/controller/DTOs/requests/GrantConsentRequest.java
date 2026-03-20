package com.tcc.consent_command_service.application.controller.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GrantConsentRequest {
    @NotBlank
    private String titularId;

    @NotNull
    @Size(min = 1)
    private List<ConsentAuthorizationRequest> authorizations;

    @NotBlank
    private String legalBasis;

    private LocalDateTime expiresAt;

    @NotBlank
    private String issuedBy;
}
