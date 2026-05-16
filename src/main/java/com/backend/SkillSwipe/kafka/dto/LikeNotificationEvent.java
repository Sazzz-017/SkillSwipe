package com.backend.SkillSwipe.kafka.dto;

import java.time.LocalDateTime;

/**
 * Event DTO published to Kafka when a user likes another user's profile.
 *
 * Why a DTO and not the entity?
 *  - Kafka messages are serialized to JSON and sent over the wire.
 *    We only include the fields the consumer needs; no JPA proxies or
 *    circular references that would break Jackson serialization.
 *
 * This class is deserialized on the consumer side, so it must remain
 * a plain POJO with a no-arg constructor.
 */
public class LikeNotificationEvent {

    /** Email address of the user who received the like — used as the mail recipient. */
    private String likedUserEmail;

    /** Display name of the user who received the like — used in the email greeting. */
    private String likedUserName;

    /** Display name of the user who sent the like — mentioned in the email body. */
    private String likedByUserName;

    /** UTC timestamp of when the like action occurred. */
    private LocalDateTime timestamp;

    // ─────────────────────────────────────────────────────────────
    // Jackson requires a no-arg constructor for deserialization
    // ─────────────────────────────────────────────────────────────
    public LikeNotificationEvent() {}

    public LikeNotificationEvent(String likedUserEmail,
                                  String likedUserName,
                                  String likedByUserName,
                                  LocalDateTime timestamp) {
        this.likedUserEmail  = likedUserEmail;
        this.likedUserName   = likedUserName;
        this.likedByUserName = likedByUserName;
        this.timestamp       = timestamp;
    }

    // ─── Getters & Setters ────────────────────────────────────────

    public String getLikedUserEmail()  { return likedUserEmail; }
    public void   setLikedUserEmail(String likedUserEmail)   { this.likedUserEmail  = likedUserEmail; }

    public String getLikedUserName()   { return likedUserName; }
    public void   setLikedUserName(String likedUserName)     { this.likedUserName   = likedUserName; }

    public String getLikedByUserName() { return likedByUserName; }
    public void   setLikedByUserName(String likedByUserName) { this.likedByUserName = likedByUserName; }

    public LocalDateTime getTimestamp()            { return timestamp; }
    public void          setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "LikeNotificationEvent{" +
                "likedUserEmail='" + likedUserEmail + '\'' +
                ", likedUserName='" + likedUserName + '\'' +
                ", likedByUserName='" + likedByUserName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
