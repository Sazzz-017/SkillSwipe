package com.backend.SkillSwipe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userId")
    int userId;

    @Column(name="userName")
    String userName;

    @Column(name="userEmail",unique = true)
    String userEmail;

    @JsonIgnore
    @Column(name="userPassword")
    String userPassword;

    @Column(name="userBio")
    String userBio;

    @Column(name="userRole")
    String userRole;

}