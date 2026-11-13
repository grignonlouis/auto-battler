package com.projet.auto_battler.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projet.auto_battler.entity.Monster;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.MonsterRepository;
import com.projet.auto_battler.service.dto.MonsterRequest;
import com.projet.auto_battler.service.dto.MonsterResponse;

/**
 * Business logic for monsters: search, creation, update, deletion and rule checks.
 * Controllers go through this class and never use the repository directly.
 */
@Service
@Transactional
public class MonsterService {

    private final MonsterRepository monsterRepository;

    /**
     * Creates the service with its repository.
     *
     * @param monsterRepository the repository used to access monsters
     */
    public MonsterService(MonsterRepository monsterRepository) {
        this.monsterRepository = monsterRepository;
    }

    /**
     * Returns the monsters sorted by name, optionally filtered by a search text.
     *
     * @param search text that must be contained in the name, or null/blank for all monsters
     * @return the matching monsters
     */
    @Transactional(readOnly = true)
    public List<MonsterResponse> findAll(String search) {
        Sort sort = Sort.by("name");
        List<Monster> monsters;
        if (search == null || search.isBlank()) {
            monsters = monsterRepository.findAll(sort);
        } else {
            monsters = monsterRepository.findByNameContainingIgnoreCase(search.trim(), sort);
        }
        return monsters.stream().map(MonsterResponse::from).toList();
    }

    /**
     * Returns one monster by its id.
     *
     * @param id the monster id
     * @return the monster
     * @throws ResourceNotFoundException if no monster has this id
     */
    @Transactional(readOnly = true)
    public MonsterResponse findById(Long id) {
        return MonsterResponse.from(getEntityOrThrow(id));
    }

    /**
     * Builds the data used to pre-fill the edit form.
     *
     * @param id the monster id
     * @return a request object holding the current values of the monster
     * @throws ResourceNotFoundException if no monster has this id
     */
    @Transactional(readOnly = true)
    public MonsterRequest getForEdit(Long id) {
        Monster monster = getEntityOrThrow(id);
        MonsterRequest request = new MonsterRequest();
        request.setName(monster.getName());
        request.setDescription(monster.getDescription());
        request.setMonsterType(monster.getMonsterType());
        request.setHealth(monster.getHealth());
        request.setDamage(monster.getDamage());
        request.setDefense(monster.getDefense());
        request.setBoss(monster.isBoss());
        return request;
    }

    /**
     * Creates a new monster.
     * The name must be unique, ignoring case.
     *
     * @param request the data of the new monster
     * @return the created monster
     * @throws BusinessRuleException if the name is already used
     */
    public MonsterResponse create(MonsterRequest request) {
        String name = request.getName().trim();
        if (monsterRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("A monster named \"" + name + "\" already exists.");
        }
        Monster monster = new Monster();
        copyToEntity(request, monster);
        return MonsterResponse.from(monsterRepository.save(monster));
    }

    /**
     * Updates an existing monster.
     * The new name must not be used by another monster, but the monster may keep its own name.
     *
     * @param id      the id of the monster to update
     * @param request the new data
     * @return the updated monster
     * @throws ResourceNotFoundException if no monster has this id
     * @throws BusinessRuleException     if another monster already uses the name
     */
    public MonsterResponse update(Long id, MonsterRequest request) {
        Monster monster = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (monsterRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("A monster named \"" + name + "\" already exists.");
        }
        copyToEntity(request, monster);
        return MonsterResponse.from(monsterRepository.save(monster));
    }

    /**
     * Deletes a monster.
     * Once the Dungeon slice exists, this method must first remove the monster from every dungeon,
     * because Dungeon owns the relation and the database would refuse the deletion otherwise.
     *
     * @param id the id of the monster to delete
     * @throws ResourceNotFoundException if no monster has this id
     */
    public void delete(Long id) {
        Monster monster = getEntityOrThrow(id);
        monsterRepository.delete(monster);
    }

    /**
     * Returns the Monster entity itself, not a DTO.
     * Intended for other services (for example the dungeon service); controllers must not use it.
     *
     * @param id the monster id
     * @return the monster entity
     * @throws ResourceNotFoundException if no monster has this id
     */
    @Transactional(readOnly = true)
    public Monster getEntityOrThrow(Long id) {
        return monsterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Monster", id));
    }

    /**
     * Copies the fields of a request into an entity, trimming the name.
     * Shared by create and update.
     *
     * @param request the source data
     * @param monster the entity to fill
     */
    private void copyToEntity(MonsterRequest request, Monster monster) {
        monster.setName(request.getName().trim());
        monster.setDescription(request.getDescription());
        monster.setMonsterType(request.getMonsterType());
        monster.setHealth(request.getHealth());
        monster.setDamage(request.getDamage());
        monster.setDefense(request.getDefense());
        monster.setBoss(request.isBoss());
    }
}