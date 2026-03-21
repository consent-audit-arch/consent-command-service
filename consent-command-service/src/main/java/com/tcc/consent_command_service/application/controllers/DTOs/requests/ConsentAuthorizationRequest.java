package com.tcc.consent_command_service.application.controllers.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
@Builder
public class ConsentAuthorizationRequest {
    @NotBlank
    private String dataCategory;

    @NotNull
    @Size(min = 1)
    private List<String> purposes;
}
