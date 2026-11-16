package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Equipment;

/**
 * Data access for equipment.
 * Query methods have no body: Spring Data derives the query from the method name.
 */
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    /**
     * Checks whether a piece of equipment with the given name already exists, ignoring case.
     *
     * @param name the name to look for
     * @return true if a piece of equipment already uses this name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Checks whether a piece of equipment other than the given one already uses this name, ignoring case.
     *
     * @param name the name to look for
     * @param id   the id of the equipment to exclude from the check
     * @return true if another piece of equipment already uses this name
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Finds the equipment whose name contains the given text, ignoring case.
     *
     * @param name the text to search for
     * @param sort the sort order of the results
     * @return the matching equipment
     */
    List<Equipment> findByNameContainingIgnoreCase(String name, Sort sort);
}