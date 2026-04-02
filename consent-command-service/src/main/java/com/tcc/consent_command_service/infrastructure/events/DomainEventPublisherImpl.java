package com.tcc.consent_command_service.infrastructure.events;

import com.tcc.consent_command_service.model.consent.events.DomainEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEventHandler;
import com.tcc.consent_command_service.model.consent.events.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private final List<DomainEventHandler<?>> handlers;

    public DomainEventPublisherImpl(List<DomainEventHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @Override
    @Transactional
    public void publish(DomainEvent event) {

        for (DomainEventHandler<?> handler : handlers) {
            if (handler.getEventType().isAssignableFrom(event.getClass())) {
                ((DomainEventHandler<DomainEvent>) handler).handle(event);
            }
        }

    }

    @Override
    @Transactional
    public void publishAll(List<DomainEvent> events) {

        for (DomainEvent event : events) {
            publish(event);
        }

    }
}
