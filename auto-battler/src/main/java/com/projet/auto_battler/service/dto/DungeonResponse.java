package com.projet.auto_battler.service.dto;

import java.util.List;

import com.projet.auto_battler.entity.Dungeon;

/**
 * Output data describing a dungeon, used by the web pages and the REST API.
 *
 * @param id          the dungeon id
 * @param name        the dungeon name
 * @param description the dungeon description
 * @param difficulty  the dungeon difficulty, from 1 to 10
 * @param unlocked    whether the dungeon is unlocked
 * @param monsters    the monsters living in the dungeon, sorted by name
 * @param threatScore the combined danger of the monsters, with the boss counting triple
 */
public record DungeonResponse(
        Long id,
        String name,
        String description,
        Integer difficulty,
        Boolean unlocked,
        List<MonsterResponse> monsters,
        int threatScore) {

    /**
     * Builds a DungeonResponse from a Dungeon entity and values computed by the service.
     *
     * @param dungeon     the entity to convert
     * @param monsters    the already converted monsters of the dungeon
     * @param threatScore the threat score computed for the dungeon
     * @return the matching response object
     */
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