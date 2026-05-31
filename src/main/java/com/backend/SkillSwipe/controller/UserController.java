package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.service.BioService;
import com.backend.SkillSwipe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    BioService bioService;

    // GET /api/users/me — returns the currently logged-in user's profile
    // Principal is populated by Spring Security from the JWT filter
    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Principal principal) {
        return userService.findByEmail(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable int id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/users/update
    @PutMapping("/update")
    public ResponseEntity<Users> updateUser(@RequestBody Users user) {
        try {
            return ResponseEntity.ok(userService.updateUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/users/search?name=
    @GetMapping("/search")
    public ResponseEntity<List<Users>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchByName(name));
    }

    // POST /api/users/{id}/generate-bio — generates an AI bio based on user's skills
    @PostMapping("/{id}/generate-bio")
    public ResponseEntity<Map<String, String>> generateBio(@PathVariable int id) {
        try {
            String bio = bioService.generateBio(id);
            return ResponseEntity.ok(Map.of("bio", bio));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
