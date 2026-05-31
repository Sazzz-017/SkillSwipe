package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.model.UsersSkills;
import com.backend.SkillSwipe.repository.UserRepo;
import com.backend.SkillSwipe.repository.UserSkillsRepo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BioService {

    private final ChatClient chatClient;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserSkillsRepo userSkillsRepo;

    public BioService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateBio(int userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<UsersSkills> skills = userSkillsRepo.findByUser(user);

        String teachSkills = skills.stream()
                .filter(s -> s.getType() == UsersSkills.UserSkillType.TEACH)
                .map(s -> s.getSkill().getSkillName() + " (" + s.getExperienceLevel().name().toLowerCase() + ")")
                .collect(Collectors.joining(", "));

        String learnSkills = skills.stream()
                .filter(s -> s.getType() == UsersSkills.UserSkillType.LEARN)
                .map(s -> s.getSkill().getSkillName() + " (" + s.getExperienceLevel().name().toLowerCase() + ")")
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "Write a short, friendly and professional bio (2-3 sentences) for a user on a skill exchange platform. " +
                "Their name is %s. They can teach: %s. They want to learn: %s. " +
                "Write in first person. Do not include any extra commentary, just the bio text.",
                user.getUserName(),
                teachSkills.isEmpty() ? "nothing listed yet" : teachSkills,
                learnSkills.isEmpty() ? "nothing listed yet" : learnSkills
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
