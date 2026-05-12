package com.backend.SkillSwipe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Column(name="user_id")
    int userId;

    @Column(name="user_name")
    String userName;

    @Column(name="user_email", unique = true)
    String userEmail;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="user_password")
    String userPassword;

    @Column(name="user_bio")
    String userBio;

    @Column(name="user_role")
    String userRole;

}