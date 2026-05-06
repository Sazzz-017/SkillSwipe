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
public class Skills {
    @Id
    @Column(name="skillId")
    int skillId;

    @Column(name="skillName",unique = true)
    String skillName;
}
