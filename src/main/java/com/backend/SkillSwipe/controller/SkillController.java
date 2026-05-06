package com.backend.SkillSwipe.controller;
import com.backend.SkillSwipe.model.Skills;
import com.backend.SkillSwipe.model.UsersSkills;
import com.backend.SkillSwipe.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
public class SkillController {
    @Autowired
    SkillService skillService;
    // GET /api/skills
    @GetMapping("/api/skills")
    public ResponseEntity<List<Skills>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }
    // POST /api/skills
    @PostMapping("/api/skills")
    public ResponseEntity<Skills> createSkill(@RequestBody Skills skill) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(skill));
    }
    // POST /api/users/skills
    @PostMapping("/api/users/skills")
    public ResponseEntity<UsersSkills> addSkillToUser(@RequestBody UsersSkills usersSkills) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(skillService.addSkillToUser(usersSkills));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // DELETE /api/users/skills/{id}
    @DeleteMapping("/api/users/skills/{id}")
    public ResponseEntity<Void> removeSkillFromUser(@PathVariable int id) {
        try {
            skillService.removeSkillFromUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
