package com.tcc.consent_command_service.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.consent_command_service.infrastructure.exceptions.OptimisticConcurrencyException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ConsentJpaRepositoryImpl implements ConsentRepository {

    private final ConsentEventJpaRepository eventRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void saveEvents(List<DomainEvent> events, Long expectedVersion) {
        long currentVersion = expectedVersion;

        for (DomainEvent domainEvent : events) {
            long versionToSave = ++currentVersion;

            if (domainEvent instanceof ConsentGrantedEvent event) {
                ConsentEventJPAEntity entity = buildEntity(event, versionToSave);
                trySave(entity);
            } else if (domainEvent instanceof ConsentRevokedEvent event) {
                ConsentEventJPAEntity entity = buildEntity(event, versionToSave);
                trySave(entity);
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

        long lastVersion = events.getLast().getVersion();

        Consent consent = Consent.builder()
                .consentId(null)
                .ownerId(ownerId)
                .version(lastVersion)
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
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T deserializeFromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ConsentEventJPAEntity buildEntity(ConsentGrantedEvent event, Long versionToSave) {
        return ConsentEventJPAEntity.builder()
                .streamId(event.getConsentId())
                .version(getNextVersion(event.getConsentId()))
                .userId(event.getOwnerId())
                .eventType("ConsentGranted")
                .dataCategory(event.getDataCategory().name())
                .finality(event.getPurpose().name())
                .payload(serializeToJson(event))
                .issuedBy(serializeToJson(event.getIssuedBy()))
                .occurredAt(event.getOccurredAt())
                .createdAt(LocalDateTime.now())
                .build();
    }
    private ConsentEventJPAEntity buildEntity(ConsentRevokedEvent event, Long versionToSave) {
        return ConsentEventJPAEntity.builder()
                .streamId(event.getConsentId())
                .version(getNextVersion(event.getConsentId()))
                .userId(event.getOwnerId())
                .eventType("ConsentRevoked")
                .dataCategory(event.getDataCategory().name())
                .finality(event.getPurpose().name())
                .payload(serializeToJson(event))
                .issuedBy(serializeToJson(event.getIssuedBy()))
                .occurredAt(event.getOccurredAt())
                .createdAt(LocalDateTime.now())
                .build();
    }


    private void trySave(ConsentEventJPAEntity entity) {
        try {
            eventRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new OptimisticConcurrencyException(
                    "Conflito de versão no stream: " + entity.getStreamId() +
                            " version: " + entity.getVersion()
            );
        }
    }
}
