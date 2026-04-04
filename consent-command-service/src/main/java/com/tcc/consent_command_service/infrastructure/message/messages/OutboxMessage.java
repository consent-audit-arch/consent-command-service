package com.tcc.consent_command_service.infrastructure.message.messages;

import lombok.Builder;

@Builder
public record OutboxMessage(
        String topic,
        String key,
        String payload
) {}