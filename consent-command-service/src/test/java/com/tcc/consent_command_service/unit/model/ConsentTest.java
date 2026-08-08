package com.tcc.consent_command_service.unit.model;

import com.tcc.consent_command_service.model.consent.entities.Consent;
import com.tcc.consent_command_service.model.consent.enuns.DataCategory;
import com.tcc.consent_command_service.model.consent.enuns.IssuerType;
import com.tcc.consent_command_service.model.consent.enuns.Purpose;
import com.tcc.consent_command_service.model.consent.events.ConsentGrantedEvent;
import com.tcc.consent_command_service.model.consent.events.ConsentRevokedEvent;
import com.tcc.consent_command_service.model.consent.events.UserDeactivatedEvent;
import com.tcc.consent_command_service.model.consent.valueObjects.ConsentAuthorization;
import com.tcc.consent_command_service.model.consent.valueObjects.Issuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ConsentTest {

    private Consent consent;
    private final Issuer issuer = Issuer.of(IssuerType.USER, 100L);

    @BeforeEach
    void setUp() {
        consent = Consent.builder()
                .consentId(UUID.randomUUID().toString())
                .ownerId(100L)
                .authorizations(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .domainEvents(new ArrayList<>())
                .build();
    }

    @Test
    void shouldGrantAuthorizationAndEmitEvent() {
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);

        assertThat(consent.getAuthorizations()).hasSize(1);
        assertThat(consent.getDomainEvents()).hasSize(1);
        assertThat(consent.getDomainEvents().getFirst()).isInstanceOf(ConsentGrantedEvent.class);
    }

    @Test
    void shouldAlwaysEmitEventEvenOnDuplicateGrant() {
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);

        assertThat(consent.getAuthorizations()).hasSize(1);
        assertThat(consent.getDomainEvents()).hasSize(2);
    }

    @Test
    void shouldAccumulatePurposesUnderSameCategory() {
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.BILLING, "CONSENT", issuer);

        assertThat(consent.getAuthorizations()).hasSize(1);
        ConsentAuthorization auth = consent.getAuthorizations().iterator().next();
        assertThat(auth.purposes()).containsExactlyInAnyOrder(Purpose.PROMOTION, Purpose.BILLING);
    }

    @Test
    void shouldRevokeAuthorizationAndEmitEvent() {
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);
        consent.clearNewDomainEvents();

        consent.revokeAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "USER_REQUEST", issuer);

        assertThat(consent.getAuthorizations()).isEmpty();
        assertThat(consent.getDomainEvents()).hasSize(1);
        assertThat(consent.getDomainEvents().getFirst()).isInstanceOf(ConsentRevokedEvent.class);
    }

    @Test
    void shouldEmitRevokeEventEvenWhenAuthorizationDoesNotExist() {
        consent.revokeAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "USER_REQUEST", issuer);

        assertThat(consent.getDomainEvents()).hasSize(1);
        assertThat(consent.getDomainEvents().getFirst()).isInstanceOf(ConsentRevokedEvent.class);
    }

    @Test
    void shouldClearDomainEvents() {
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);
        consent.clearNewDomainEvents();

        assertThat(consent.getDomainEvents()).isEmpty();
    }

    @Test
    void shouldDeactivateUserAndRevokeAllAuthorizations() {
        Issuer systemIssuer = Issuer.of(IssuerType.SYSTEM, 0L);
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.PROMOTION, "CONSENT", issuer);
        consent.grantAuthorization(DataCategory.PERSONAL_DATA, Purpose.BILLING, "CONSENT", issuer);
        consent.grantAuthorization(DataCategory.FINANCIAL_DATA, Purpose.BILLING, "CONSENT", issuer);
        consent.clearNewDomainEvents();

        int revoked = consent.deactivateUser("USER_ACCOUNT_DEACTIVATED", systemIssuer);

        assertThat(revoked).isEqualTo(3);
        assertThat(consent.getAuthorizations()).isEmpty();
        assertThat(consent.getDomainEvents()).hasSize(4);
        long revokedEvents = consent.getDomainEvents().stream()
                .filter(e -> e instanceof ConsentRevokedEvent)
                .count();
        assertThat(revokedEvents).isEqualTo(3);
        assertThat(consent.getDomainEvents())
                .filteredOn(e -> e instanceof UserDeactivatedEvent)
                .hasSize(1);
    }

    @Test
    void shouldReturnZeroRevokedWhenDeactivatingEmptyConsent() {
        Issuer systemIssuer = Issuer.of(IssuerType.SYSTEM, 0L);
        int revoked = consent.deactivateUser("USER_ACCOUNT_DEACTIVATED", systemIssuer);

        assertThat(revoked).isEqualTo(0);
        assertThat(consent.getDomainEvents()).hasSize(1);
        assertThat(consent.getDomainEvents().getFirst()).isInstanceOf(UserDeactivatedEvent.class);
    }
}
