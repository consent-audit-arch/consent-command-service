package com.tcc.consent_command_service.fixtures.requests;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.ConsentAuthorizationRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.IssuerRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.RevokeConsentRequest;
import com.tcc.consent_command_service.model.consent.enuns.IssuerType;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;

import java.time.LocalDateTime;
import java.util.List;

public class RevokeConsentRequestFixture {

    public static RevokeConsentRequest defaultValues() {
        return RevokeConsentRequest.builder()
                .ownerId(100L)
                .authorizations(List.of(
                        ConsentAuthorizationRequest.builder()
                                .dataCategory("Dados Pessoais")
                                .purposes(List.of("Promoção"))
                                .build(),
                        ConsentAuthorizationRequest.builder()
                                .dataCategory("Dados Contratuais")
                                .purposes(List.of("Promoção", "Análise"))
                                .build()
                ))
                .reason("User requested data removal")
                .occurredAt(LocalDateTime.now())
                .issuedBy(IssuerRequest.builder()
                        .id(100L)
                        .issuer("SYSTEM")
                        .build())
                .build();
    }

    public static RevokeConsentRequest partialRevoke() {
        return RevokeConsentRequest.builder()
                .ownerId(100L)
                .authorizations(List.of(
                        ConsentAuthorizationRequest.builder()
                                .dataCategory("Dados Pessoais")
                                .purposes(List.of("Promoção"))
                                .build()
                ))
                .reason("Partial opt-out")
                .occurredAt(LocalDateTime.now())
                .issuedBy(IssuerRequest.builder()
                        .id(100L)
                        .issuer("SYSTEM")
                        .build())
                .build();
    }
}