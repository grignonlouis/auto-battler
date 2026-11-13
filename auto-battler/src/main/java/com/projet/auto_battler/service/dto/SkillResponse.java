package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Skill;
import com.projet.auto_battler.entity.SkillType;

/**
 * Output data describing a skill, used by the web pages and the REST API.
 * Entities are never exposed directly to avoid lazy loading errors and infinite JSON loops.
 *
 * @param id          the skill id
 * @param name        the skill name
 * @param description the optional description
 * @param skillType   the kind of skill
 * @param power       damage, healing or shield/poison value
 */
public record SkillResponse(
        Long id,
        String name,
        String description,
        SkillType skillType,
        int power) {  

    /**
     * Converts a Skill entity into a SkillResponse by copying its fields.
     *
     * @param skill the entity to convert
     * @return the matching response object
     */
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