package com.backend.SkillSwipe.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Users;
import java.util.List;
import java.util.Optional;
public interface UserRepo extends JpaRepository<Users, Integer> {
    List<Users> findByUserNameContainingIgnoreCase(String name);
    Optional<Users> findByUserEmail(String email);
}
