package com.backend.SkillSwipe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
public class Users {
    @Id
    @Column(name="userId")
    int userId;

    @Column(name="userName")
    String userName;

    @Column(name="userEmail",unique = true)
    String userEmail;

    @Column(name="userPassword")
    String userPassword;

    @Column(name="userBio")
    String userBio;

    @Column(name="userRole")
    String userRole;

}