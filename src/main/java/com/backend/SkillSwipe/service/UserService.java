package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    public Optional<Users> findById(int id) {
        return userRepo.findById(id);
    }

    public Users updateUser(Users updatedUser) {
        Users existing = userRepo.findById(updatedUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedUser.getUserId()));
        if (updatedUser.getUserName() != null) existing.setUserName(updatedUser.getUserName());
        if (updatedUser.getUserEmail() != null) existing.setUserEmail(updatedUser.getUserEmail());
        if (updatedUser.getUserPassword() != null) existing.setUserPassword(updatedUser.getUserPassword());
        if (updatedUser.getUserBio() != null) existing.setUserBio(updatedUser.getUserBio());
        if (updatedUser.getUserRole() != null) existing.setUserRole(updatedUser.getUserRole());
        return userRepo.save(existing);
    }

    public List<Users> searchByName(String name) {
        return userRepo.findByUserNameContainingIgnoreCase(name);
    }
}
