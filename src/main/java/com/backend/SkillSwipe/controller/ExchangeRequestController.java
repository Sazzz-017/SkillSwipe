package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.model.ExchangeRequests;
import com.backend.SkillSwipe.service.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class ExchangeRequestController {

    @Autowired
    ExchangeRequestService exchangeRequestService;

    // POST /api/requests
    @PostMapping
    public ResponseEntity<ExchangeRequests> createRequest(@RequestBody ExchangeRequests request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exchangeRequestService.createRequest(request));
    }

    // GET /api/requests/sent?userId=
    @GetMapping("/sent")
    public ResponseEntity<List<ExchangeRequests>> getSentRequests(@RequestParam int userId) {
        try {
            return ResponseEntity.ok(exchangeRequestService.getSentRequests(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/requests/received?userId=
    @GetMapping("/received")
    public ResponseEntity<List<ExchangeRequests>> getReceivedRequests(@RequestParam int userId) {
        try {
            return ResponseEntity.ok(exchangeRequestService.getReceivedRequests(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/requests/{id}/accept
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable int id) {
        try {
            return ResponseEntity.ok(exchangeRequestService.acceptRequest(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/requests/{id}/reject
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable int id) {
        try {
            return ResponseEntity.ok(exchangeRequestService.rejectRequest(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/requests/{id}/complete
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeRequest(@PathVariable int id) {
        try {
            return ResponseEntity.ok(exchangeRequestService.completeRequest(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

