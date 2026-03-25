package com.tcc.consent_command_service.model.consent.valueObjects;

import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import lombok.Builder;

import java.util.Set;

@Builder
public record ConsentAuthorization(
        DataCategory dataCategory,
        Set<Purpose> purposes) {

    public ConsentAuthorization(DataCategory dataCategory, Set<Purpose> purposes) {
        if (dataCategory == null) {
            throw new IllegalArgumentException("dataCategory não pode ser nulo.");
        }
        if (purposes == null || purposes.isEmpty()) {
            throw new IllegalArgumentException("purposes não pode estar vazio.");
        }
        this.dataCategory = dataCategory;
        this.purposes = Set.copyOf(purposes);
    }


    public static ConsentAuthorization of(DataCategory dataCategory, Set<Purpose> purposes) {
        return new ConsentAuthorization(dataCategory, purposes);
    }
}
