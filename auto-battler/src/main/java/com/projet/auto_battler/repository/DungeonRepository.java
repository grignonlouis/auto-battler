package com.projet.auto_battler.repository;

import com.projet.auto_battler.entity.Dungeon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;

public interface DungeonRepository extends JpaRepository<Dungeon, Long> {

    @Override
    @EntityGraph(attributePaths = "monsters")
    List<Dungeon> findAll();
}