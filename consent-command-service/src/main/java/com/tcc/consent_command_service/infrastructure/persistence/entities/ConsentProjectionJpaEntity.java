package com.tcc.consent_command_service.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consent_projection")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentProjectionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "data_category", nullable = false)
    private String dataCategory;

    @Column(name = "finality", nullable = false)
    private String finality;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "legal_basis", nullable = false)
    private String legalBasis;

    @Column(name = "granted_at")
    private LocalDateTime grantedAt;


    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "last_event_id", nullable = false)
    private Long lastEventId;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
