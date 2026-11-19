package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Dungeon;

public interface DungeonRepository extends JpaRepository<Dungeon, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Override
    @EntityGraph(attributePaths = "monsters")
    List<Dungeon> findAll(Sort sort);

    @EntityGraph(attributePaths = "monsters")
    List<Dungeon> findByNameContainingIgnoreCase(String name, Sort sort);

    List<Dungeon> findByMonstersId(Long monsterId);
}