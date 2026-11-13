package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Monster;

/**
 * Data access for monsters.
 * Query methods have no body: Spring Data derives the query from the method name.
 */
public interface MonsterRepository extends JpaRepository<Monster, Long> {

    /**
     * Checks whether a monster with the given name already exists, ignoring case.
     *
     * @param name the name to look for
     * @return true if a monster already uses this name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Checks whether a monster other than the given one already uses this name, ignoring case.
     *
     * @param name the name to look for
     * @param id   the id of the monster to exclude from the check
     * @return true if another monster already uses this name
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Finds the monsters whose name contains the given text, ignoring case.
     *
     * @param name the text to search for
     * @param sort the sort order of the results
     * @return the matching monsters
     */
    List<Monster> findByNameContainingIgnoreCase(String name, Sort sort);
}