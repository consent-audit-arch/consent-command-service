package com.tcc.consent_command_service.model.consent.events;

public interface DomainEventHandler<T extends DomainEvent> {
    void handle(T event);
    Class<T> getEventType();
}
