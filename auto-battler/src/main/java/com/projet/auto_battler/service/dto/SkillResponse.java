// record : id, name, description, skillType, power, cooldown, duration.
// Une méthode statique de fabrique from(Skill) qui copie les champs.
// Jamais d'entité renvoyée par l'API : uniquement ce record.

package com.projet.auto_battler.service.dto;

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
