package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.model.Reviews;
import com.backend.SkillSwipe.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    // POST /api/reviews
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Reviews review) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(review));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/reviews/user/{id}
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Reviews>> getReviewsForUser(@PathVariable int id) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsForUser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

