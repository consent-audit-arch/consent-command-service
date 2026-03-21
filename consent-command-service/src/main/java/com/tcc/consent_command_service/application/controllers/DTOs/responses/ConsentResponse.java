package com.tcc.consent_command_service.application.controllers.DTOs.responses;

import lombok.Builder;

@Builder
public record ConsentResponse (
        Long eventId,
        String streamId
) {
}
