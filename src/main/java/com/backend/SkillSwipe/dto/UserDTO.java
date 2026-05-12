package com.backend.SkillSwipe.dto;

import com.backend.SkillSwipe.model.UsersSkills;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private int userId;
    private String userName;
    private String userEmail;
    private String userBio;
    private List<UsersSkills> skills;
}
