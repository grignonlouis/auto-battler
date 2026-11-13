package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Monster;
import com.projet.auto_battler.entity.MonsterType;

/**
 * Output data describing a monster, used by the web pages and the REST API.
 *
 * @param id          the monster id
 * @param name        the monster name
 * @param description the optional description
 * @param monsterType the kind of monster
 * @param health      the health points
 * @param damage      the damage dealt by its attacks
 * @param defense     the damage reduction
 * @param boss        true if the monster is a boss
 */
public record MonsterResponse(
        Long id,
        String name,
        String description,
        MonsterType monsterType,
        int health,
        int damage,
        int defense,
        boolean boss) {

    /**
     * Converts a Monster entity into a MonsterResponse by copying its fields.
     *
     * @param monster the entity to convert
     * @return the matching response object
     */
    public static MonsterResponse from(Monster monster) {
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                monster.getDescription(),
                monster.getMonsterType(),
                monster.getHealth(),
                monster.getDamage(),
                monster.getDefense(),
                monster.isBoss());
    }
}