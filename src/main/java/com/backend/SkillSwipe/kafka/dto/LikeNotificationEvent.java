package com.backend.SkillSwipe.kafka.dto;

import java.time.LocalDateTime;

public class LikeNotificationEvent {

    private String likedUserEmail;
    private String likedUserName;
    private String likedByUserName;
    private LocalDateTime timestamp;

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
