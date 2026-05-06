package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.model.Message;
import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.repository.MessageRepo;
import com.backend.SkillSwipe.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    UserRepo userRepo;

    // Send a message
    public Message sendMessage(Message message) {
        userRepo.findById(message.getSender().getUserId())
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + message.getSender().getUserId()));
        userRepo.findById(message.getReceiver().getUserId())
                .orElseThrow(() -> new RuntimeException("Receiver not found with id: " + message.getReceiver().getUserId()));
        return messageRepo.save(message);
    }

    // Get conversation between two users
    public List<Message> getConversation(int userId1, int userId2) {
        Users user1 = userRepo.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId1));
        Users user2 = userRepo.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId2));
        return messageRepo.findConversation(user1, user2);
    }

    // Get unread messages for a user
    public List<Message> getUnreadMessages(int userId) {
        Users receiver = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return messageRepo.findByReceiverAndSeenFalse(receiver);
    }
}

