package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Reviews;

public interface ReviewsRepo extends JpaRepository<Reviews, Integer> {
}
