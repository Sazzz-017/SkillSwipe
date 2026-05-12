package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.dto.SessionProposalDTO;
import com.backend.SkillSwipe.model.ExchangeRequests;
import com.backend.SkillSwipe.model.SessionProposal;
import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.repository.ExchangeRequestsRepo;
import com.backend.SkillSwipe.repository.SessionProposalRepo;
import com.backend.SkillSwipe.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm")
            .optionalStart().appendPattern(":ss").optionalEnd()
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    @Autowired
    private SessionProposalRepo sessionProposalRepo;

    @Autowired
    private ExchangeRequestsRepo exchangeRequestsRepo;

    @Autowired
    private UserRepo userRepo;

    public Optional<SessionProposalDTO> getProposal(int sessionId) {
        ExchangeRequests session = exchangeRequestsRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        List<SessionProposal> proposals = sessionProposalRepo.findBySession(session);
        return proposals.stream()
                .max(Comparator.comparing(SessionProposal::getUpdatedAt))
                .map(this::toDTO);
    }

    @Transactional
    public SessionProposalDTO propose(int sessionId, String meetingLink, String proposedTimeStr) {
        ExchangeRequests session = exchangeRequestsRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        Users proposedBy = getAuthenticatedUser();
        LocalDateTime proposedTime = LocalDateTime.parse(proposedTimeStr, FLEXIBLE_FORMATTER);

        // Delete existing proposals for this session before creating new one
        sessionProposalRepo.deleteBySession(session);

        SessionProposal proposal = new SessionProposal();
        proposal.setSession(session);
        proposal.setProposedBy(proposedBy);
        proposal.setMeetingLink(meetingLink);
        proposal.setProposedTime(proposedTime);
        proposal.setStatus(SessionProposal.Status.PENDING);

        return toDTO(sessionProposalRepo.save(proposal));
    }

    @Transactional
    public SessionProposalDTO acceptProposal(int sessionId) {
        ExchangeRequests session = exchangeRequestsRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        SessionProposal proposal = sessionProposalRepo.findBySession(session).stream()
                .max(Comparator.comparing(SessionProposal::getUpdatedAt))
                .orElseThrow(() -> new RuntimeException("No proposal found for session: " + sessionId));

        proposal.setStatus(SessionProposal.Status.CONFIRMED);
        proposal.setUpdatedAt(LocalDateTime.now());

        return toDTO(sessionProposalRepo.save(proposal));
    }

    @Transactional
    public SessionProposalDTO counterProposal(int sessionId, String meetingLink, String proposedTimeStr) {
        ExchangeRequests session = exchangeRequestsRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        Users proposedBy = getAuthenticatedUser();
        LocalDateTime proposedTime = LocalDateTime.parse(proposedTimeStr, FLEXIBLE_FORMATTER);

        sessionProposalRepo.deleteBySession(session);

        SessionProposal proposal = new SessionProposal();
        proposal.setSession(session);
        proposal.setProposedBy(proposedBy);
        proposal.setMeetingLink(meetingLink);
        proposal.setProposedTime(proposedTime);
        proposal.setStatus(SessionProposal.Status.PENDING);

        return toDTO(sessionProposalRepo.save(proposal));
    }

    private Users getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private SessionProposalDTO toDTO(SessionProposal p) {
        SessionProposalDTO dto = new SessionProposalDTO();
        dto.setId(p.getId());
        dto.setSessionId(p.getSession().getId());
        dto.setMeetingLink(p.getMeetingLink());
        dto.setProposedTime(p.getProposedTime() != null ? p.getProposedTime().toString() : null);
        dto.setStatus(p.getStatus().name());

        SessionProposalDTO.ProposedByDTO proposedByDTO = new SessionProposalDTO.ProposedByDTO();
        proposedByDTO.setUserId(p.getProposedBy().getUserId());
        proposedByDTO.setUserName(p.getProposedBy().getUserName());
        dto.setProposedBy(proposedByDTO);

        return dto;
    }
}
