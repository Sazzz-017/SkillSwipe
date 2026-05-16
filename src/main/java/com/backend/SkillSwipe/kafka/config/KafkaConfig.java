package com.backend.SkillSwipe.kafka.config;

import com.backend.SkillSwipe.kafka.dto.LikeNotificationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConfig — centralises all Kafka bean definitions.
 *
 * Why Kafka?
 *  Email sending is I/O-bound and slow (~200–500 ms per SMTP call).
 *  Doing it synchronously inside the HTTP request thread would:
 *    1. Increase API response latency for the swiper.
 *    2. Couple availability of the mail server to the core "like" flow.
 *  Kafka decouples the two concerns:
 *    • The producer (SwipeService) publishes an event in <1 ms and returns.
 *    • The consumer picks up the event from the topic and sends the email
 *      asynchronously, without blocking the HTTP thread.
 *
 * Producer vs Consumer
 *  - Producer  → writes LikeNotificationEvent messages to the Kafka topic.
 *  - Consumer  → reads those messages and hands them to the email service.
 */
@Configuration
public class KafkaConfig {

    // ─── Injected from application.properties ────────────────────────────────

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topic.like-notification}")
    private String likeNotificationTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    // ─── Topic ───────────────────────────────────────────────────────────────

    /**
     * Declares the topic so Spring creates it automatically on startup
     * if it doesn't already exist (requires auto-create enabled on the broker
     * or admin rights — both are true for local Docker Kafka).
     */
    @Bean
    public NewTopic likeNotificationTopic() {
        return new NewTopic(likeNotificationTopic, 1, (short) 1);
    }

    // ─── Producer ────────────────────────────────────────────────────────────

    @Bean
    public ProducerFactory<String, LikeNotificationEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Key is a plain String (e.g. "userId")
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Value is serialized to JSON using Jackson
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Retry on transient broker errors
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, LikeNotificationEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ─── Consumer ────────────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, LikeNotificationEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Tell the JsonDeserializer which class to deserialize the value into
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LikeNotificationEvent.class.getName());
        // Trust the package that contains our DTO
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.backend.SkillSwipe.kafka.dto");
        // Start from the latest offset so we don't reprocess old events on restart
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LikeNotificationEvent>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LikeNotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
