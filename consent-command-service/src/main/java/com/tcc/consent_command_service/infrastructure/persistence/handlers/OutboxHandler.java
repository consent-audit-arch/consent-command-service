package com.tcc.consent_command_service.infrastructure.persistence.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.consent_command_service.infrastructure.persistence.entities.OutboxJpaEntity;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.OutboxJpaRepository;
import com.tcc.consent_command_service.model.consent.events.ConsentGrantedEvent;
import com.tcc.consent_command_service.model.consent.events.ConsentRevokedEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxHandler implements DomainEventHandler<DomainEvent> {

    private final OutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof ConsentGrantedEvent e) {
            save(e.getConsentId(), "ConsentGranted", "consent.granted", e);
        } else if (event instanceof ConsentRevokedEvent e) {
            save(e.getConsentId(), "ConsentRevoked", "consent.revoked", e);
        }
    }

    @Override
    public Class<DomainEvent> getEventType() {
        return DomainEvent.class;
    }

    private void save(String streamId, String eventType, String topic, Object payload) {
        outboxRepository.save(OutboxJpaEntity.builder()
                .streamId(streamId)
                .eventType(eventType)
                .topic(topic)
                .payload(serializeToJson(payload))
                .published(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private String serializeToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
