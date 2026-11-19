package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Monster;
import com.projet.auto_battler.entity.MonsterType;

public record MonsterResponse(
        Long id,
        String name,
        String description,
        MonsterType monsterType,
        int health,
        int damage,
        int defense
        ) {

    public static MonsterResponse from(Monster monster) {
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                monster.getDescription(),
                monster.getMonsterType(),
                monster.getHealth(),
                monster.getDamage(),
                monster.getDefense());
    }
}