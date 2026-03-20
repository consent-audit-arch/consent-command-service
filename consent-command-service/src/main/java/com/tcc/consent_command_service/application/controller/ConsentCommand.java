package com.tcc.consent_command_service.application.controller;

import com.tcc.consent_command_service.application.controller.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controller.DTOs.responses.ConsentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${base-url}")
public class ConsentCommand {

    @PostMapping
    public ResponseEntity<ConsentResponse> grantConsent (
            @Validated @RequestBody GrantConsentRequest request) {



        return null;
    }

}
