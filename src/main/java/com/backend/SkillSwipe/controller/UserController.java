package com.backend.SkillSwipe.controller;

import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

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
}
