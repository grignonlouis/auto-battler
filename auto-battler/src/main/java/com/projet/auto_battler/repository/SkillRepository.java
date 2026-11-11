// interface qui étend JpaRepository<Skill, Long>.
// Méthodes dérivées à déclarer (aucune implémentation) :
//  - existsByNameIgnoreCase(String name)                       -> unicité à la création
//  - existsByNameIgnoreCaseAndIdNot(String name, Long id)      -> unicité à la modification
//  - recherche par nom (contient, insensible à la casse), pour ?search=

package com.projet.auto_battler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    List<Skill> findByNameContainingIgnoreCase(String name);    
}
