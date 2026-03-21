package com.tcc.consent_command_service.application.mappers;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.ConsentAuthorizationRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.requests.IssuerRequest;
import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.IssuerType;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.valueObjects.ConsentAuthorization;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ConsentMapper {

    @Mapping(target = "consentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Consent grantConsentRequestToDomain(GrantConsentRequest request);

    @Mapping(target = "dataCategory", source = "dataCategory")
    @Mapping(target = "purposes", source = "purposes")
    ConsentAuthorization authorizationRequestToDomain(ConsentAuthorizationRequest request);

    @Mapping(target = "issuer", source = "issuer")
    Issuer issuerRequestToDomain(IssuerRequest request);

    default DataCategory stringToDataCategory(String value) {
        return value == null ? null : DataCategory.of(value);
    }

    default Purpose stringToPurpose(String value) {
        return value == null ? null : Purpose.of(value);
    }

    default IssuerType stringToIssuer(String value) {
        return value == null ? null : IssuerType.of(value);
    }
}
