package com.tcc.consent_command_service.integration;

import com.tcc.consent_command_service.application.services.ConsentService;
import com.tcc.consent_command_service.config.TestFlywayConfig;
import com.tcc.consent_command_service.fixtures.requests.GrantConsentRequestFixture;
import com.tcc.consent_command_service.fixtures.requests.RevokeConsentRequestFixture;
import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentEventJPAEntity;
import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentProjectionJpaEntity;
import com.tcc.consent_command_service.infrastructure.persistence.entities.OutboxJpaEntity;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.ConsentEventJpaRepository;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.ConsentProjectionJpaRepository;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.OutboxJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(TestFlywayConfig.class)
public class ConsentCommandServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private ConsentEventJpaRepository eventRepository;

    @Autowired
    private OutboxJpaRepository outboxRepository;

    @Autowired
    private ConsentProjectionJpaRepository projectionRepository;

    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll();
        projectionRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void shouldGrantConsentAndPersistEvents() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);

        assertThat(events).hasSize(3);
        assertThat(events).allMatch(e -> e.getEventType().equals("ConsentGranted"));
        assertThat(events.get(0).getDataCategory()).isEqualTo("PERSONAL_DATA");
        assertThat(events.get(0).getFinality()).isEqualTo("PROMOTION");
        assertThat(events.get(1).getDataCategory()).isEqualTo("CONTRACT_DATA");
        assertThat(events.get(2).getDataCategory()).isEqualTo("CONTRACT_DATA");
    }

    @Test
    void shouldPersistOutboxEntriesOnGrantConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        List<OutboxJpaEntity> outboxEntries = outboxRepository.findByPublishedFalse();

        assertThat(outboxEntries).hasSize(3);
        assertThat(outboxEntries).allMatch(o -> o.getEventType().equals("ConsentGranted"));
        assertThat(outboxEntries).allMatch(o -> o.getTopic().equals("consent.granted"));
        assertThat(outboxEntries).allMatch(o -> !o.getPublished());
    }

    @Test
    void shouldUpdateProjectionOnGrantConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(100L);

        assertThat(projections).hasSize(3);
        assertThat(projections).allMatch(p -> p.getStatus().equals("GRANTED"));
        assertThat(projections).allMatch(p -> p.getLegalBasis().equals("CONSENT"));
        assertThat(projections).allMatch(p -> p.getGrantedAt() != null);
        assertThat(projections).allMatch(p -> p.getRevokedAt() == null);
    }

    @Test
    void shouldBeIdempotentOnDuplicateGrantConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);
        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(100L);


        assertThat(events).hasSize(6);
        assertThat(projections).hasSize(3);
        assertThat(projections).allMatch(p -> p.getStatus().equals("GRANTED"));
    }

    @Test
    void shouldRevokeConsentAndPersistEvents() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);

        long revokedEvents = events.stream()
                .filter(e -> e.getEventType().equals("ConsentRevoked"))
                .count();

        assertThat(revokedEvents).isEqualTo(3);
    }

    @Test
    void shouldPersistOutboxEntriesOnRevokeConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        outboxRepository.deleteAll();

        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());

        List<OutboxJpaEntity> outboxEntries = outboxRepository.findByPublishedFalse();

        assertThat(outboxEntries).hasSize(3);
        assertThat(outboxEntries).allMatch(o -> o.getEventType().equals("ConsentRevoked"));
        assertThat(outboxEntries).allMatch(o -> o.getTopic().equals("consent.revoked"));
        assertThat(outboxEntries).allMatch(o -> !o.getPublished());
    }

    @Test
    void shouldUpdateProjectionStatusToRevokedOnRevokeConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());

        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(100L);

        assertThat(projections).hasSize(3);
        assertThat(projections).allMatch(p -> p.getStatus().equals("REVOKED"));
        assertThat(projections).allMatch(p -> p.getRevokedAt() != null);
    }

    @Test
    void shouldPartiallyRevokeConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.partialRevoke());

        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(100L);

        long revoked = projections.stream()
                .filter(p -> p.getStatus().equals("REVOKED"))
                .count();
        long granted = projections.stream()
                .filter(p -> p.getStatus().equals("GRANTED"))
                .count();

        assertThat(revoked).isEqualTo(1);
        assertThat(granted).isEqualTo(2);
    }

    @Test
    void shouldBeIdempotentOnDuplicateRevokeConsent() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);
        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(100L);

        long revokedEvents = events.stream()
                .filter(e -> e.getEventType().equals("ConsentRevoked"))
                .count();

        assertThat(revokedEvents).isEqualTo(6);
        assertThat(projections).hasSize(3);
        assertThat(projections).allMatch(p -> p.getStatus().equals("REVOKED"));
    }


    @Test
    void shouldReGrantAfterRevoke() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);
        List<ConsentProjectionJpaEntity> projections = projectionRepository.findByUserId(100L);

        long grantedEvents = events.stream()
                .filter(e -> e.getEventType().equals("ConsentGranted"))
                .count();
        long revokedEvents = events.stream()
                .filter(e -> e.getEventType().equals("ConsentRevoked"))
                .count();

        assertThat(grantedEvents).isEqualTo(6);
        assertThat(revokedEvents).isEqualTo(3);
        assertThat(projections).hasSize(3);
        assertThat(projections).allMatch(p -> p.getStatus().equals("GRANTED"));
    }

    @Test
    void shouldMaintainCorrectEventOrderInEventStore() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);

        assertThat(events).hasSize(6);

        assertThat(events.subList(0, 3)).allMatch(e -> e.getEventType().equals("ConsentGranted"));
        assertThat(events.subList(3, 6)).allMatch(e -> e.getEventType().equals("ConsentRevoked"));
    }

    @Test
    void shouldHaveCorrectVersionSequenceAfterGrantAndRevoke() {
        consentService.grantConsent(GrantConsentRequestFixture.defaultValues());
        consentService.revokeConsent(RevokeConsentRequestFixture.defaultValues());

        List<ConsentEventJPAEntity> events = eventRepository.findByUserIdOrderByVersion(100L);

        for (int i = 0; i < events.size(); i++) {
            assertThat(events.get(i).getVersion()).isEqualTo(i + 1);
        }
    }
}
