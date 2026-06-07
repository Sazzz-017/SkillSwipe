package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.model.Skills;
import com.backend.SkillSwipe.model.UsersSkills;
import com.backend.SkillSwipe.repository.SkillRepo;
import com.backend.SkillSwipe.repository.UserRepo;
import com.backend.SkillSwipe.repository.UserSkillsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    @Autowired
    SkillRepo skillRepo;

    @Autowired
    UserSkillsRepo userSkillsRepo;

    @Autowired
    UserRepo userRepo;

    public List<Skills> getAllSkills() {
        return skillRepo.findAll();
    }

    public Skills createSkill(Skills skill) {
        return skillRepo.save(skill);
    }

    public UsersSkills addSkillToUser(UsersSkills usersSkills) {
        userRepo.findById(usersSkills.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + usersSkills.getUser().getUserId()));
        skillRepo.findById(usersSkills.getSkill().getSkillId())
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + usersSkills.getSkill().getSkillId()));
        return userSkillsRepo.save(usersSkills);
    }

    public void removeSkillFromUser(int id) {
        if (!userSkillsRepo.existsById(id)) {
            throw new RuntimeException("UserSkill not found with id: " + id);
        }
        userSkillsRepo.deleteById(id);
    }
}


