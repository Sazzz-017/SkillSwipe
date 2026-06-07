package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.dto.UserDTO;
import com.backend.SkillSwipe.model.ExchangeRequests;
import com.backend.SkillSwipe.model.SwipeAction;
import com.backend.SkillSwipe.service.SwipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/swipe")
public class SwipeController {

    @Autowired
    private SwipeService swipeService;

    @GetMapping("/candidates")
    public ResponseEntity<List<UserDTO>> getCandidates(@RequestParam int userId) {
        return ResponseEntity.ok(swipeService.getCandidates(userId));
    }

    @PostMapping("/action")
    public ResponseEntity<SwipeAction> recordAction(@RequestBody Map<String, Object> body) {
        int swiperId = (int) body.get("swiperId");
        int targetId = (int) body.get("targetId");
        String action = (String) body.get("action");
        return ResponseEntity.ok(swipeService.recordAction(swiperId, targetId, action));
    }

    @GetMapping("/likes-received")
    public ResponseEntity<List<UserDTO>> getLikesReceived(@RequestParam int userId) {
        return ResponseEntity.ok(swipeService.getLikesReceived(userId));
    }

    @PostMapping("/match")
    public ResponseEntity<ExchangeRequests> acceptLike(@RequestBody Map<String, Integer> body) {
        int swiperId = body.get("swiperId");
        int targetId = body.get("targetId");
        return ResponseEntity.ok(swipeService.acceptLike(swiperId, targetId));
    }
}
