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

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final EmailNotificationService emailNotificationService;

    public KafkaConsumerService(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @KafkaListener(
            topics     = "${app.kafka.topic.like-notification}",
            groupId    = "${spring.kafka.consumer.group-id}",
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
            log.error("[Kafka Consumer] Unexpected error while processing event for '{}': {}",
                    event.getLikedUserEmail(), e.getMessage(), e);
        }
    }
}
