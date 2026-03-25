package com.tcc.consent_command_service.model.consent.events;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent {

    private final LocalDateTime occurredAt;

    public DomainEvent () {
        this.occurredAt = LocalDateTime.now();
    }

}
