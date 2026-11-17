package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Dungeon;

/**
 * Data access for dungeons.
 * Query methods have no body: Spring Data derives the query from the method name.
 */
public interface DungeonRepository extends JpaRepository<Dungeon, Long> {

    /**
     * Checks whether a dungeon with the given name already exists, ignoring case.
     *
     * @param name the name to look for
     * @return true if a dungeon already uses this name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Checks whether a dungeon other than the given one already uses this name, ignoring case.
     *
     * @param name the name to look for
     * @param id   the id of the dungeon to exclude from the check
     * @return true if another dungeon already uses this name
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Returns every dungeon, loading its monsters in the same query.
     *
     * @param sort the sort order of the results
     * @return all the dungeons
     */
    @Override
    @EntityGraph(attributePaths = "monsters")
    List<Dungeon> findAll(Sort sort);

    /**
     * Finds the dungeons whose name contains the given text, ignoring case, loading their monsters in the same query.
     *
     * @param name the text to search for
     * @param sort the sort order of the results
     * @return the matching dungeons
     */
    @EntityGraph(attributePaths = "monsters")
    List<Dungeon> findByNameContainingIgnoreCase(String name, Sort sort);

    /**
     * Finds the dungeons that contain a given monster.
     * Used to unlink a monster from its dungeons before the monster is deleted.
     *
     * @param monsterId the id of the monster
     * @return the dungeons containing this monster
     */
    List<Dungeon> findByMonstersId(Long monsterId);
}