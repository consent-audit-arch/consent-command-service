package com.tcc.consent_command_service.infrastructure.message.Producer;

import com.tcc.consent_command_service.infrastructure.message.messages.OutboxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class OutboxEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, String>> publish(OutboxMessage message) {
        log.info("Publicando no tópico={} key={}", message.topic(), message.key());
        return kafkaTemplate.send(message.topic(), message.key(), message.payload());
    }
}
