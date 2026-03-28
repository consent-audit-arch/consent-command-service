package com.tcc.consent_command_service.application.controllers.DTOs.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuerRequest {

    @NotNull
    private String issuer;

    @NotNull
    private Long id;
}
