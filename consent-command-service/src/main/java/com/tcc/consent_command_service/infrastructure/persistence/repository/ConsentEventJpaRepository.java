package com.tcc.consent_command_service.infrastructure.persistence.repository;

import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentEventJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentEventJpaRepository extends JpaRepository<ConsentEventJpaRepository, Long> {

    List<ConsentEventJPAEntity> findByStreamIdOrderByVersion (String streamId);

    Long findMaxVersionByStreamId(String streamId);
}
