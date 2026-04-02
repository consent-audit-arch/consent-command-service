package com.tcc.consent_command_service.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentEventJPAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String streamId;

    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String dataCategory;

    @Column(nullable = false)
    private String finality;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "issued_by", columnDefinition = "jsonb", nullable = false)
    private String issuedBy;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
