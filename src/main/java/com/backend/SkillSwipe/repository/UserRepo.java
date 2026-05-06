package com.backend.SkillSwipe.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Users;
import java.util.List;
public interface UserRepo extends JpaRepository<Users, Integer> {
    List<Users> findByUserNameContainingIgnoreCase(String name);
}
