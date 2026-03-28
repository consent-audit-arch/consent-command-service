package com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository;

import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentProjectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentProjectionJpaRepository extends JpaRepository<ConsentProjectionJpaEntity, Long> {

    Optional<ConsentProjectionJpaEntity> findByUserIdAndDataCategoryAndFinality(
            Long userId, String dataCategory, String finality);

    List<ConsentProjectionJpaEntity> findByUserId(Long userId);
}
