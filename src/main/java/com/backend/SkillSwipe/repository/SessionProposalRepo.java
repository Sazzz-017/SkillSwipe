package com.backend.SkillSwipe.repository;

import com.backend.SkillSwipe.model.ExchangeRequests;
import com.backend.SkillSwipe.model.SessionProposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionProposalRepo extends JpaRepository<SessionProposal, Long> {
    List<SessionProposal> findBySession(ExchangeRequests session);
    void deleteBySession(ExchangeRequests session);
}
