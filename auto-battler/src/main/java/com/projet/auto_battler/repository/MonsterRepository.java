package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Monster;

public interface MonsterRepository extends JpaRepository<Monster, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Monster> findByNameContainingIgnoreCase(String name, Sort sort);
}