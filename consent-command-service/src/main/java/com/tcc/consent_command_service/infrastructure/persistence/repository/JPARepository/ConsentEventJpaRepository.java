package com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository;

import com.tcc.consent_command_service.infrastructure.persistence.entities.ConsentEventJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentEventJpaRepository extends JpaRepository<ConsentEventJPAEntity, Long> {

    List<ConsentEventJPAEntity> findByUserIdOrderByVersion (Long streamId);

    ConsentEventJPAEntity findTopByStreamIdAndDataCategoryAndFinalityOrderByVersionDesc(
            String streamId, String dataCategory, String finality);
}
