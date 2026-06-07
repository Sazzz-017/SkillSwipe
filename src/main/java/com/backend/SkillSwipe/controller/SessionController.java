package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.dto.SessionProposalDTO;
import com.backend.SkillSwipe.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/proposal")
    public ResponseEntity<SessionProposalDTO> getProposal(@RequestParam int sessionId) {
        return sessionService.getProposal(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/propose")
    public ResponseEntity<SessionProposalDTO> propose(@RequestBody Map<String, Object> body) {
        int sessionId = (int) body.get("sessionId");
        String meetingLink = (String) body.get("meetingLink");
        String proposedTime = (String) body.get("proposedTime");
        return ResponseEntity.ok(sessionService.propose(sessionId, meetingLink, proposedTime));
    }

    @PutMapping("/proposal/accept")
    public ResponseEntity<SessionProposalDTO> acceptProposal(@RequestBody Map<String, Integer> body) {
        int sessionId = body.get("sessionId");
        return ResponseEntity.ok(sessionService.acceptProposal(sessionId));
    }

    @PutMapping("/proposal/counter")
    public ResponseEntity<SessionProposalDTO> counterProposal(@RequestBody Map<String, Object> body) {
        int sessionId = (int) body.get("sessionId");
        String meetingLink = (String) body.get("meetingLink");
        String proposedTime = (String) body.get("proposedTime");
        return ResponseEntity.ok(sessionService.counterProposal(sessionId, meetingLink, proposedTime));
    }
}
