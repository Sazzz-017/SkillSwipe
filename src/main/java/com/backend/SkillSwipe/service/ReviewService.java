package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.model.Reviews;
import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.repository.ReviewsRepo;
import com.backend.SkillSwipe.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    ReviewsRepo reviewsRepo;

    @Autowired
    UserRepo userRepo;

    // POST /api/reviews
    public Reviews createReview(Reviews review) {
        userRepo.findById(review.getReviewer().getUserId())
                .orElseThrow(() -> new RuntimeException("Reviewer not found with id: " + review.getReviewer().getUserId()));
        userRepo.findById(review.getReviewedUser().getUserId())
                .orElseThrow(() -> new RuntimeException("Reviewed user not found with id: " + review.getReviewedUser().getUserId()));
        if (review.getReviewer().getUserId() == review.getReviewedUser().getUserId()) {
            throw new RuntimeException("A user cannot review themselves");
        }
        return reviewsRepo.save(review);
    }

    // GET /api/reviews/user/{id}
    public List<Reviews> getReviewsForUser(int userId) {
        Users reviewedUser = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return reviewsRepo.findByReviewedUser(reviewedUser);
    }
}

