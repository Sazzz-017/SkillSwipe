package com.backend.SkillSwipe.kafka.consumer;

import com.backend.SkillSwipe.kafka.dto.LikeNotificationEvent;
import com.backend.SkillSwipe.kafka.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * KafkaConsumerService — the read side of the event-driven flow.
 *
 * Responsibility: listen to the like-notification Kafka topic, receive
 * deserialized LikeNotificationEvent messages, and delegate to the
 * EmailNotificationService to send the actual email.
 *
 * Why a separate consumer class?
 *  - Single Responsibility: the consumer only handles event reception
 *    and orchestration; email construction lives in EmailNotificationService.
 *  - Testability: we can test consumption logic independently of SMTP.
 *  - Scalability: if throughput grows, we can increase concurrency by
 *    changing `concurrency` in @KafkaListener without touching business logic.
 *
 * Async flow recap:
 *  SwipeService (HTTP thread) → Kafka Topic → THIS CONSUMER (background thread)
 *                                                     ↓
 *                                           EmailNotificationService (SMTP)
 */
@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final EmailNotificationService emailNotificationService;

    public KafkaConsumerService(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * Listens to the user-like-notification topic and processes incoming events.
     *
     * @param event      The deserialized LikeNotificationEvent from Kafka.
     * @param partition  Kafka partition the message was consumed from (for logging).
     * @param offset     Kafka offset of the message (for logging/debugging).
     */
    @KafkaListener(
            topics     = "${app.kafka.topic.like-notification}",
            groupId    = "${spring.kafka.consumer.group-id}",
            // Use the typed ContainerFactory defined in KafkaConfig
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeLikeNotification(
            @Payload LikeNotificationEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[Kafka Consumer] Event received → partition={}, offset={}, event={}",
                partition, offset, event);

        try {
            emailNotificationService.sendLikeNotificationEmail(
                    event.getLikedUserEmail(),
                    event.getLikedUserName(),
                    event.getLikedByUserName()
            );
        } catch (Exception e) {
            // Catch-all so the consumer does not crash and stops processing
            // subsequent messages. In production, consider a dead-letter topic (DLT).
            log.error("[Kafka Consumer] Unexpected error while processing event for '{}': {}",
                    event.getLikedUserEmail(), e.getMessage(), e);
        }
    }
}
