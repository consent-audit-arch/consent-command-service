package com.tcc.consent_command_service.application.controllers.DTOs.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssuerRequest {

    @NotNull
    private String issuer;

    @NotNull
    private Long id;
}
