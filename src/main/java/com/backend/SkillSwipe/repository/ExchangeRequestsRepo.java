package com.backend.SkillSwipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.SkillSwipe.model.ExchangeRequests;
import com.backend.SkillSwipe.model.Users;

import java.util.List;

public interface ExchangeRequestsRepo extends JpaRepository<ExchangeRequests, Integer> {
    List<ExchangeRequests> findBySender(Users sender);
    List<ExchangeRequests> findByReceiver(Users receiver);
}
