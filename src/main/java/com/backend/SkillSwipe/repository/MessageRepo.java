package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.backend.SkillSwipe.model.Message;
import com.backend.SkillSwipe.model.Users;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp ASC")
    List<Message> findConversation(Users user1, Users user2);

    List<Message> findByReceiverAndSeenFalse(Users receiver);
}
