package com.tcc.consent_command_service.infrastructure.persistence.handlers;

import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentEventJPAEntity;
import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentProjectionJpaEntity;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.ConsentEventJpaRepository;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.ConsentProjectionJpaRepository;
import com.tcc.consent_command_service.model.consent.events.ConsentGrantedEvent;
import com.tcc.consent_command_service.model.consent.events.ConsentRevokedEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProjectionHandler implements DomainEventHandler<DomainEvent> {

    private final ConsentProjectionJpaRepository projectionRepository;
    private final ConsentEventJpaRepository eventRepository;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof ConsentGrantedEvent e) {
            handleGranted(e);
        } else if (event instanceof ConsentRevokedEvent e) {
            handleRevoked(e);
        }
    }

    @Override
    public Class<DomainEvent> getEventType() {
        return DomainEvent.class;
    }

    private void handleGranted(ConsentGrantedEvent event) {
        ConsentEventJPAEntity eventEntity = eventRepository
                .findTopByStreamIdAndDataCategoryAndFinalityOrderByVersionDesc(
                        event.getConsentId(),
                        event.getDataCategory().name(),
                        event.getPurpose().name()
                );

        projectionRepository.findByUserIdAndDataCategoryAndFinality(
                event.getOwnerId(),
                event.getDataCategory().name(),
                event.getPurpose().name()
        ).ifPresentOrElse(
                existing -> {
                    existing.setStatus("GRANTED");
                    existing.setGrantedAt(event.getOccurredAt());
                    existing.setRevokedAt(null);
                    existing.setLastEventId(eventEntity.getId());
                    existing.setVersion(existing.getVersion() + 1);
                    existing.setUpdatedAt(LocalDateTime.now());
                },
                () -> projectionRepository.save(ConsentProjectionJpaEntity.builder()
                        .userId(event.getOwnerId())
                        .dataCategory(event.getDataCategory().name())
                        .finality(event.getPurpose().name())
                        .status("GRANTED")
                        .legalBasis("CONSENT")
                        .grantedAt(event.getOccurredAt())
                        .lastEventId(eventEntity.getId())
                        .version(1L)
                        .updatedAt(LocalDateTime.now())
                        .build())
        );
    }

    private void handleRevoked(ConsentRevokedEvent event) {
        ConsentEventJPAEntity eventEntity = eventRepository
                .findTopByStreamIdAndDataCategoryAndFinalityOrderByVersionDesc(
                        event.getConsentId(),
                        event.getDataCategory().name(),
                        event.getPurpose().name()
                );

        projectionRepository.findByUserIdAndDataCategoryAndFinality(
                event.getOwnerId(),
                event.getDataCategory().name(),
                event.getPurpose().name()
        ).ifPresent(existing -> {
            existing.setStatus("REVOKED");
            existing.setRevokedAt(event.getOccurredAt());
            existing.setLastEventId(eventEntity.getId());
            existing.setVersion(existing.getVersion() + 1);
            existing.setUpdatedAt(LocalDateTime.now());
        });
    }
}
