package com.backend.SkillSwipe.kafka.producer;

import com.backend.SkillSwipe.kafka.dto.LikeNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value("${app.kafka.topic.like-notification}")
    private String likeNotificationTopic;

    private final KafkaTemplate<String, LikeNotificationEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, LikeNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishLikeNotification(LikeNotificationEvent event) {
        String messageKey = event.getLikedUserEmail();

        try {
            CompletableFuture<SendResult<String, LikeNotificationEvent>> future =
                    kafkaTemplate.send(likeNotificationTopic, messageKey, event);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("[Kafka Producer] Could not deliver event for '{}' — Kafka may be offline. Email notification skipped. Reason: {}",
                            event.getLikedUserEmail(), ex.getMessage());
                } else {
                    log.info("[Kafka Producer] Event published → topic='{}', partition={}, offset={}, event={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event);
                }
            });
        } catch (Exception e) {
            log.warn("[Kafka Producer] Kafka unavailable — like notification skipped for '{}'. Start Kafka to enable email notifications.",
                    event.getLikedUserEmail());
        }
    }
}
