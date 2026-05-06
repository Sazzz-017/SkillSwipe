package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.UsersSkills;
import com.backend.SkillSwipe.model.Users;

import java.util.List;

public interface UserSkillsRepo extends JpaRepository<UsersSkills, Integer> {
    List<UsersSkills> findByUser(Users user);
}
