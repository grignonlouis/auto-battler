package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Skill;
import com.projet.auto_battler.entity.SkillType;

public record SkillResponse(
        Long id,
        String name,
        String description,
        SkillType skillType,
        int power) {  

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getDescription(),
                skill.getSkillType(),
                skill.getPower()
        );
    }
}