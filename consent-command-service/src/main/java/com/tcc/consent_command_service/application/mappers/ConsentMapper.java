package com.tcc.consent_command_service.application.mappers;

import com.tcc.consent_command_service.application.controller.DTOs.requests.ConsentAuthorizationRequest;
import com.tcc.consent_command_service.application.controller.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controller.DTOs.responses.ConsentResponse;
import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.valueObjects.valueObjects.ConsentAuthorization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsentMapper {

    @Mapping(target = "consentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "domainEvents", ignore = true)
    Consent grantConsentRequestToDomain(GrantConsentRequest request);

    @Mapping(target = "dataCategory", source = "dataCategory")
    @Mapping(target = "purposes", source = "purposes")
    ConsentAuthorization authorizationRequestToDomain(ConsentAuthorizationRequest request);

    ConsentResponse domainToResponse(Consent consent);

    default DataCategory stringToDataCategory(String value) {
        return value == null ? null : DataCategory.valueOf(value);
    }

    default Purpose stringToPurpose(String value) {
        return value == null ? null : Purpose.valueOf(value);
    }

    default String dataCategoryToString(DataCategory value) {
        return value == null ? null : value.name();
    }

    default String purposeToString(Purpose value) {
        return value == null ? null : value.name();
    }
}
