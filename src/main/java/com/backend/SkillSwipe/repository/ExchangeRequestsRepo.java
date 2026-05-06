package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.ExchangeRequests;

public interface ExchangeRequestsRepo extends JpaRepository<ExchangeRequests, Integer> {
}
