package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Skills;
public interface SkillRepo extends JpaRepository<Skills, Integer> {
}
