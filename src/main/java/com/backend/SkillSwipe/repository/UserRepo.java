package com.backend.SkillSwipe.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Users;
public interface UserRepo extends JpaRepository<Users, Integer> {
}
