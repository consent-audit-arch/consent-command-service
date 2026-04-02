package com.tcc.consent_command_service.model.consent.events;

import java.util.List;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    void publishAll(List<DomainEvent> events);

}
