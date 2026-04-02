package com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository;

import com.tcc.consent_command_service.infrastructure.persistence.entities.OutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity, Long> {

    List<OutboxJpaEntity> findByPublishedFalse();

}
