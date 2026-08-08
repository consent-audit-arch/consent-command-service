package com.tcc.consent_command_service.application.controllers;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.DeactivateConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.RevokeConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.responses.ConsentResponse;
import com.tcc.consent_command_service.application.controllers.DTOs.responses.DeactivateConsentResponse;
import com.tcc.consent_command_service.application.services.ConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base-url}")
public class ConsentCommand {

    private final ConsentService consentService;

    @PostMapping
    public ResponseEntity<ConsentResponse> grantConsent (
            @Validated @RequestBody GrantConsentRequest request) {

        log.info("Received grant access request for user: {}", request.getOwnerId());

        consentService.grantConsent(request);

        log.info("Granted access for user: {}", request.getOwnerId());

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<DeactivateConsentResponse> deactivate(
            @Validated @RequestBody DeactivateConsentRequest request) {

        log.info("Received deactivate request for user: {}", request.getOwnerId());
        DeactivateConsentResponse response = consentService.deactivateConsent(request);
        log.info("Deactivated user {}: {} consents revoked", request.getOwnerId(), response.consentsRevoked());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{ownerId}/revoke")
    public ResponseEntity<ConsentResponse> revokeConsent (
            @Validated @RequestBody RevokeConsentRequest request,
            @PathVariable Long ownerId) {

        log.info("Received revoke access request for user: {}", ownerId);

        consentService.revokeConsent(request);

        log.info("Revoked access for user: {}", ownerId);

        return ResponseEntity.accepted().build();
    }

}
