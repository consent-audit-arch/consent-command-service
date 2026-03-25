package com.tcc.consent_command_service.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentEventJPAEntity;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.ConsentEventJpaRepository;
import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.events.ConsentGrantedEvent;
import com.tcc.consent_command_service.model.consent.events.ConsentRevokedEvent;
import com.tcc.consent_command_service.model.consent.events.DomainEvent;
import com.tcc.consent_command_service.model.consent.repositories.ConsentRepository;
import com.tcc.consent_command_service.model.consent.valueObjects.ConsentAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ConsentJpaRepositoryImpl implements ConsentRepository {

    private final ConsentEventJpaRepository eventRepository;


    @Override
    public void saveEvents(List<DomainEvent> events) {
        for (DomainEvent domainEvent : events) {

            if (domainEvent instanceof ConsentGrantedEvent event) {

                ConsentEventJPAEntity eventEntity = ConsentEventJPAEntity.builder()
                        .streamId(event.getConsentId())
                        .version(getNextVersion(event.getConsentId()))
                        .userId(event.getOwnerId())
                        .eventType("ConsentGranted")
                        .dataCategory(event.getDataCategory().name())
                        .finality(event.getPurpose().name())
                        .payload(serializeToJson(event))
                        .issuedBy(event.getIssuedBy().toString())
                        .occurredAt(event.getOccurredAt())
                        .createdAt(LocalDateTime.now())
                        .build();

                eventRepository.save(eventEntity);
            }

            else if (domainEvent instanceof ConsentRevokedEvent event) {

                ConsentEventJPAEntity eventEntity = ConsentEventJPAEntity.builder()
                        .streamId(event.getConsentId())
                        .version(getNextVersion(event.getConsentId()))
                        .userId(event.getOwnerId())
                        .eventType("ConsentRevoked")
                        .dataCategory(event.getDataCategory().name())
                        .finality(event.getPurpose().name())
                        .payload(serializeToJson(event))
                        .issuedBy(event.getIssuedBy().toString())
                        .occurredAt(event.getOccurredAt())
                        .createdAt(LocalDateTime.now())
                        .build();

                eventRepository.save(eventEntity);
            }
        }
    }

    @Override
    public Consent findByOwnerId(Long ownerId) {
        List<ConsentEventJPAEntity> events = eventRepository
                .findByUserIdOrderByVersion(ownerId);

        if (events.isEmpty()) {
            return null;
        }

        return reconstructConsentFromEvents(events, ownerId);
    }

    private Consent reconstructConsentFromEvents(
            List<ConsentEventJPAEntity> events,
            Long ownerId) {

        Consent consent = Consent.builder()
                .consentId(null)
                .ownerId(ownerId)
                .authorizations(new HashSet<>())
                .createdAt(events.getFirst().getCreatedAt())
                .domainEvents(new ArrayList<>())
                .build();

        for (ConsentEventJPAEntity eventEntity : events) {
            if ("ConsentGranted".equals(eventEntity.getEventType())) {
                ConsentGrantedEvent event = deserializeFromJson(
                        eventEntity.getPayload(),
                        ConsentGrantedEvent.class
                );

                Optional<ConsentAuthorization> existing = consent.getAuthorizations().stream()
                        .filter(auth -> auth.dataCategory().equals(event.getDataCategory()))
                        .findFirst();

                if (existing.isPresent()) {
                    Set<Purpose> updatedPurposes = new HashSet<>(existing.get().purposes());
                    updatedPurposes.add(event.getPurpose());
                    consent.getAuthorizations().remove(existing.get());
                    consent.getAuthorizations().add(ConsentAuthorization.of(event.getDataCategory(), updatedPurposes));
                } else {
                    consent.getAuthorizations().add(ConsentAuthorization.of(
                            event.getDataCategory(),
                            new HashSet<>(Set.of(event.getPurpose()))
                    ));
                }
            }
            else if ("ConsentRevoked".equals(eventEntity.getEventType())) {
                ConsentRevokedEvent event = deserializeFromJson(
                        eventEntity.getPayload(),
                        ConsentRevokedEvent.class
                );

                Optional<ConsentAuthorization> existing = consent.getAuthorizations().stream()
                        .filter(auth -> auth.dataCategory().equals(event.getDataCategory()))
                        .findFirst();

                if (existing.isPresent()) {
                    Set<Purpose> updatedPurposes = new HashSet<>(existing.get().purposes());
                    updatedPurposes.remove(event.getPurpose());
                    consent.getAuthorizations().remove(existing.get());

                    if (!updatedPurposes.isEmpty()) {
                        consent.getAuthorizations().add(ConsentAuthorization.of(event.getDataCategory(), updatedPurposes));
                    }
                }
            }
        }

        consent.setConsentId(events.getFirst().getStreamId());

        return consent;
    }

    private Long getNextVersion(String streamId) {
        Long maxVersion = eventRepository.findMaxVersionByStreamId(streamId);
        return maxVersion == null ? 1L : maxVersion + 1L;
    }

    private String serializeToJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T deserializeFromJson(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
