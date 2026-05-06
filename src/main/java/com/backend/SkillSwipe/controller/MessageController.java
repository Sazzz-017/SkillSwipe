package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.model.Message;
import com.backend.SkillSwipe.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    MessageService messageService;

    // POST /api/messages
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(message));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/messages/conversation?userId1=&userId2=
    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getConversation(@RequestParam int userId1, @RequestParam int userId2) {
        try {
            return ResponseEntity.ok(messageService.getConversation(userId1, userId2));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/messages/unread?userId=
    @GetMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestParam int userId) {
        try {
            return ResponseEntity.ok(messageService.getUnreadMessages(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

