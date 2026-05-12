package com.backend.SkillSwipe.dto;

import lombok.Data;

@Data
public class SessionProposalDTO {
    private Long id;
    private int sessionId;
    private ProposedByDTO proposedBy;
    private String meetingLink;
    private String proposedTime;
    private String status;

    @Data
    public static class ProposedByDTO {
        private int userId;
        private String userName;
    }
}
