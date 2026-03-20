package com.tcc.consent_command_service.application.controller.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
public class ConsentAuthorizationRequest {
    @NotBlank
    private String dataCategory;  // String do HTTP

    @NotNull
    @Size(min = 1)
    private List<String> purposes;  // Strings do HTTP
}
