package com.tcc.consent_command_service.application.controllers;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.responses.ConsentResponse;
import com.tcc.consent_command_service.application.services.ConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base-url}")
public class ConsentCommand {

    private final ConsentService grantConsentService;

    @PostMapping
    public ResponseEntity<ConsentResponse> grantConsent (
            @Validated @RequestBody GrantConsentRequest request) {

        log.info("Received grant access request for user: {}", request.getOwnerId());

        grantConsentService.grantConsent(request);

        log.info("Granted access for user: {}", request.getOwnerId());

        return ResponseEntity.accepted().build();
    }

}
