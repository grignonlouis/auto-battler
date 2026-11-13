package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Skill;

/**
 * Data access for skills.
 * Query methods have no body: Spring Data derives the query from the method name.
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Checks whether a skill with the given name already exists, ignoring case.
     *
     * @param name the name to look for
     * @return true if a skill already uses this name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Checks whether a skill other than the given one already uses this name, ignoring case.
     * Used on update so that a skill can keep its own name.
     *
     * @param name the name to look for
     * @param id   the id of the skill to exclude from the check
     * @return true if another skill already uses this name
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Finds the skills whose name contains the given text, ignoring case.
     *
     * @param name the text to search for
     * @param sort the sort order of the results
     * @return the matching skills
     */
    List<Skill> findByNameContainingIgnoreCase(String name, Sort sort);
}