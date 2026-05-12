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

    // GET /api/swipe/candidates?userId=
    @GetMapping("/candidates")
    public ResponseEntity<List<UserDTO>> getCandidates(@RequestParam int userId) {
        return ResponseEntity.ok(swipeService.getCandidates(userId));
    }

    // POST /api/swipe/action
    // Body: { "swiperId": 1, "targetId": 2, "action": "LIKE" }
    @PostMapping("/action")
    public ResponseEntity<SwipeAction> recordAction(@RequestBody Map<String, Object> body) {
        int swiperId = (int) body.get("swiperId");
        int targetId = (int) body.get("targetId");
        String action = (String) body.get("action");
        return ResponseEntity.ok(swipeService.recordAction(swiperId, targetId, action));
    }

    // GET /api/swipe/likes-received?userId=
    @GetMapping("/likes-received")
    public ResponseEntity<List<UserDTO>> getLikesReceived(@RequestParam int userId) {
        return ResponseEntity.ok(swipeService.getLikesReceived(userId));
    }

    // POST /api/swipe/match
    // Body: { "swiperId": 2, "targetId": 1 }
    @PostMapping("/match")
    public ResponseEntity<ExchangeRequests> acceptLike(@RequestBody Map<String, Integer> body) {
        int swiperId = body.get("swiperId");
        int targetId = body.get("targetId");
        return ResponseEntity.ok(swipeService.acceptLike(swiperId, targetId));
    }
}
