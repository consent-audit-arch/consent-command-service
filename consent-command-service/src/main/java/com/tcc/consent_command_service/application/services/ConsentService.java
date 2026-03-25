package com.tcc.consent_command_service.application.services;

import com.tcc.consent_command_service.application.controllers.DTOs.requests.GrantConsentRequest;
import com.tcc.consent_command_service.application.controllers.DTOs.responses.ConsentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ConsentService {

    @Transactional
    public ConsentResponse grantConsent(GrantConsentRequest consent) {





        return null;
    }

}
