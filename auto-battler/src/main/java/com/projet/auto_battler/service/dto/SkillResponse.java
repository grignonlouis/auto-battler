// record : id, name, description, skillType, power, cooldown, duration.
// Une méthode statique de fabrique from(Skill) qui copie les champs.
// Jamais d'entité renvoyée par l'API : uniquement ce record.

package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Skill;
import com.projet.auto_battler.entity.SkillType;

public record SkillResponse(Long id, String name, String description, SkillType skillType, int power, int cooldown, int duration) {
    public static SkillResponse from(Skill skill) {
        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getDescription(),
            skill.getSkillType(),
            skill.getPower(),
            skill.getCooldown(),
            skill.getDuration()
        );
    }
}
