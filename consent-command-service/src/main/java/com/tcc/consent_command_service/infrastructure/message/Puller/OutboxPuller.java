package com.tcc.consent_command_service.infrastructure.message.Puller;

import com.tcc.consent_command_service.infrastructure.message.Producer.OutboxEventProducer;
import com.tcc.consent_command_service.infrastructure.message.messages.OutboxMessage;
import com.tcc.consent_command_service.infrastructure.persistence.entities.OutboxJpaEntity;
import com.tcc.consent_command_service.infrastructure.persistence.repository.JPARepository.OutboxJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPuller {

    private final OutboxJpaRepository outboxRepository;
    private final OutboxEventProducer producer;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval:5000}")
    @Transactional
    @ConditionalOnProperty(name = "outbox.puller.enabled", havingValue = "true", matchIfMissing = true)
    public void pollAndPublish() {
        List<OutboxJpaEntity> events = outboxRepository.findByPublishedFalseOrderByCreatedAtAsc();
        if (events.isEmpty()) return;

        log.info("Outbox Puller: {} evento(s) pendente(s)", events.size());

        for (OutboxJpaEntity event : events) {
            try {
                producer.publish(createMessage(event)).get();
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
                outboxRepository.save(event);
                log.info("Evento id={} publicado com sucesso", event.getId());
            } catch (Exception ex) {
                log.error("Falha ao publicar evento id={}: {}", event.getId(), ex.getMessage());
                break;
            }
        }
    }

    private OutboxMessage createMessage(OutboxJpaEntity events) {
        return OutboxMessage.builder()
                .topic(events.getTopic())
                .key(events.getStreamId())
                .payload(events.getPayload())
                .build();
    }
}
