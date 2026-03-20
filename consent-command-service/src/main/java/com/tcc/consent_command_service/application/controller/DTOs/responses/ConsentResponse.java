package com.tcc.consent_command_service.application.controller.DTOs.responses;

import lombok.Builder;

@Builder
public record ConsentResponse (
        Long eventId,
        String streamId
) {
}
