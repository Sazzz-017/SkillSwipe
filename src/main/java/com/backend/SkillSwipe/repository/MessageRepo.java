package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.Message;

public interface MessageRepo extends JpaRepository<Message, Integer> {
}
