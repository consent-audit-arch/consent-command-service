package com.tcc.consent_command_service.application.controllers.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentAuthorizationRequest {
    @NotBlank
    private String dataCategory;

    @NotNull
    @Size(min = 1)
    private List<String> purposes;
}
