package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.UsersSkills;

public interface UserSkillsRepo extends JpaRepository<UsersSkills, Integer> {
}
