package com.projet.auto_battler.service.dto;

import java.util.List;

import com.projet.auto_battler.entity.Dungeon;

public record DungeonResponse(
        Long id,
        String name,
        String description,
        Integer difficulty,
        Boolean unlocked,
        List<MonsterResponse> monsters,
        int threatScore) {

    public static DungeonResponse from(Dungeon dungeon, List<MonsterResponse> monsters, int threatScore) {
        return new DungeonResponse(
                dungeon.getId(),
                dungeon.getName(),
                dungeon.getDescription(),
                dungeon.getDifficulty(),
                dungeon.isUnlocked(),
                monsters,
                threatScore);
    }
}