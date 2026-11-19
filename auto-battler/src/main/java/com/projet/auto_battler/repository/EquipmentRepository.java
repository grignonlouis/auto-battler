package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Equipment> findByNameContainingIgnoreCase(String name, Sort sort);
}