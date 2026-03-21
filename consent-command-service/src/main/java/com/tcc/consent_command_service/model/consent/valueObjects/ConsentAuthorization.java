package com.tcc.consent_command_service.model.consent.valueObjects;

import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ConsentAuthorization {

    DataCategory dataCategory;
    List<Purpose> purposes;


    public ConsentAuthorization(DataCategory dataCategory, List<Purpose> purposes) {
        if (dataCategory == null) {
            throw new IllegalArgumentException("dataCategory não pode ser nulo.");
        }
        if (purposes == null || purposes.isEmpty()) {
            throw new IllegalArgumentException("purposes não pode estar vazio.");
        }
        this.dataCategory = dataCategory;
        this.purposes = List.copyOf(purposes);
    }


    public static ConsentAuthorization of(DataCategory dataCategory, List<Purpose> purposes) {
        return new ConsentAuthorization(dataCategory, purposes);
    }
}
