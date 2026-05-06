package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Reviews;
import com.backend.SkillSwipe.model.Users;

import java.util.List;

public interface ReviewsRepo extends JpaRepository<Reviews, Integer> {
    List<Reviews> findByReviewedUser(Users reviewedUser);
    List<Reviews> findByReviewer(Users reviewer);
}
