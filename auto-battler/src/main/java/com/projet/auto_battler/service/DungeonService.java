package com.projet.auto_battler.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.entity.Monster;
import com.projet.auto_battler.entity.MonsterType;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.DungeonRepository;
import com.projet.auto_battler.service.dto.DungeonRequest;
import com.projet.auto_battler.service.dto.DungeonResponse;
import com.projet.auto_battler.service.dto.MonsterResponse;

/**
 * Business logic for dungeons: search, creation, update, deletion, monster association and rule checks.
 * Controllers go through this class and never use the repository directly.
 * Rules enforced here: unique dungeon names and at most one boss per dungeon.
 */
@Service
@Transactional
public class DungeonService {

    private final DungeonRepository dungeonRepository;
    private final MonsterService monsterService;

    /**
     * Creates the service with its dependencies.
     *
     * @param dungeonRepository the repository used to access dungeons
     * @param monsterService    the service used to load monster entities
     */
    public DungeonService(DungeonRepository dungeonRepository, MonsterService monsterService) {
        this.dungeonRepository = dungeonRepository;
        this.monsterService = monsterService;
    }

    /**
     * Returns the dungeons sorted by name, optionally filtered by a search text.
     *
     * @param search text that must be contained in the name, or null/blank for everything
     * @return the matching dungeons
     */
    @Transactional(readOnly = true)
    public List<DungeonResponse> findAll(String search) {
        Sort sort = Sort.by("name");
        List<Dungeon> dungeons;
        if (search == null || search.isBlank()) {
            dungeons = dungeonRepository.findAll(sort);
        } else {
            dungeons = dungeonRepository.findByNameContainingIgnoreCase(search.trim(), sort);
        }
        return dungeons.stream().map(this::toResponse).toList();
    }

    /**
     * Returns one dungeon by its id.
     *
     * @param id the dungeon id
     * @return the dungeon
     * @throws ResourceNotFoundException if no dungeon has this id
     */
    @Transactional(readOnly = true)
    public DungeonResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    /**
     * Builds the data used to pre-fill the edit form, including the ids of the current monsters.
     *
     * @param id the dungeon id
     * @return a request object holding the current values of the dungeon
     * @throws ResourceNotFoundException if no dungeon has this id
     */
    @Transactional(readOnly = true)
    public DungeonRequest getForEdit(Long id) {
        Dungeon dungeon = getEntityOrThrow(id);
        DungeonRequest request = new DungeonRequest();
        request.setName(dungeon.getName());
        request.setDescription(dungeon.getDescription());
        request.setDifficulty(dungeon.getDifficulty());
        request.setUnlocked(dungeon.isUnlocked());
        request.setMonsterIds(dungeon.getMonsters().stream()
                .map(Monster::getId)
                .collect(Collectors.toSet()));
        return request;
    }

    /**
     * Creates a new dungeon with the selected monsters.
     * The name must be unique, ignoring case, and the monsters may include at most one boss.
     *
     * @param request the data of the new dungeon
     * @return the created dungeon
     * @throws BusinessRuleException     if the name is used or the monsters include several bosses
     * @throws ResourceNotFoundException if a monster id does not exist
     */
    public DungeonResponse create(DungeonRequest request) {
        String name = request.getName().trim();
        if (dungeonRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("name", "A dungeon named \"" + name + "\" already exists.");
        }
        Dungeon dungeon = new Dungeon();
        copyToEntity(request, dungeon);
        return toResponse(dungeonRepository.save(dungeon));
    }

    /**
     * Updates an existing dungeon, replacing its monsters by the selected ones.
     * The new name must not be used by another dungeon, but the dungeon may keep its own name.
     *
     * @param id      the id of the dungeon to update
     * @param request the new data
     * @return the updated dungeon
     * @throws ResourceNotFoundException if the dungeon or one of the monsters does not exist
     * @throws BusinessRuleException     if the name is used by another dungeon or the monsters include several bosses
     */
    public DungeonResponse update(Long id, DungeonRequest request) {
        Dungeon dungeon = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (dungeonRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("name", "A dungeon named \"" + name + "\" already exists.");
        }
        copyToEntity(request, dungeon);
        return toResponse(dungeonRepository.save(dungeon));
    }

    /**
     * Deletes a dungeon.
     * Its links with monsters are removed automatically because Dungeon owns the relation;
     * the monsters themselves are kept.
     * Once battles can reference dungeons, this method must refuse the deletion when the dungeon has battles,
     * so that the history is never lost by accident.
     *
     * @param id the id of the dungeon to delete
     * @throws ResourceNotFoundException if no dungeon has this id
     */
    public void delete(Long id) {
        Dungeon dungeon = getEntityOrThrow(id);
        dungeonRepository.delete(dungeon);
    }

    /**
     * Adds a monster to a dungeon. Adding a monster that is already there changes nothing.
     * A dungeon can contain at most one boss.
     *
     * @param dungeonId the id of the dungeon
     * @param monsterId the id of the monster to add
     * @return the updated dungeon
     * @throws ResourceNotFoundException if the dungeon or the monster does not exist
     * @throws BusinessRuleException     if the monster is a second boss
     */
    public DungeonResponse addMonster(Long dungeonId, Long monsterId) {
        Dungeon dungeon = getEntityOrThrow(dungeonId);
        Monster monster = monsterService.getEntityOrThrow(monsterId);
        boolean alreadyInside = dungeon.getMonsters().stream()
                .anyMatch(existing -> existing.getId().equals(monsterId));
        if (!alreadyInside) {
            List<Monster> afterAdding = new ArrayList<>(dungeon.getMonsters());
            afterAdding.add(monster);
            checkAtMostOneBoss(afterAdding);
            dungeon.getMonsters().add(monster);
        }
        return toResponse(dungeon);
    }

    /**
     * Removes a monster from a dungeon. Removing a monster that is not there changes nothing.
     *
     * @param dungeonId the id of the dungeon
     * @param monsterId the id of the monster to remove
     * @return the updated dungeon
     * @throws ResourceNotFoundException if the dungeon or the monster does not exist
     */
    public DungeonResponse removeMonster(Long dungeonId, Long monsterId) {
        Dungeon dungeon = getEntityOrThrow(dungeonId);
        monsterService.getEntityOrThrow(monsterId);
        dungeon.getMonsters().removeIf(existing -> existing.getId().equals(monsterId));
        return toResponse(dungeon);
    }

    /**
     * Computes the threat score of a dungeon.
     * Each monster counts for health / 10 + damage, and the boss counts three times.
     *
     * @param id the dungeon id
     * @return the threat score
     * @throws ResourceNotFoundException if no dungeon has this id
     */
    @Transactional(readOnly = true)
    public int threatScore(Long id) {
        return computeThreatScore(getEntityOrThrow(id));
    }

    /**
     * Returns the Dungeon entity itself, not a DTO.
     * Intended for other services (for example the battle service); controllers must not use it.
     *
     * @param id the dungeon id
     * @return the dungeon entity
     * @throws ResourceNotFoundException if no dungeon has this id
     */
    @Transactional(readOnly = true)
    public Dungeon getEntityOrThrow(Long id) {
        return dungeonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dungeon", id));
    }

    /**
     * Copies a request into an entity: the fields, then the monsters.
     * The monsters are checked before anything is changed, so a rule violation leaves the entity untouched.
     * Shared by create and update.
     *
     * @param request the source data
     * @param dungeon the entity to fill
     */
    private void copyToEntity(DungeonRequest request, Dungeon dungeon) {
        Set<Monster> monsters = loadMonsters(request.getMonsterIds());
        checkAtMostOneBoss(monsters);
        dungeon.setName(request.getName().trim());
        dungeon.setDescription(request.getDescription().trim());
        dungeon.setDifficulty(request.getDifficulty());
        dungeon.setUnlocked(request.getUnlocked());
        dungeon.getMonsters().clear();
        dungeon.getMonsters().addAll(monsters);
    }

    /**
     * Loads the monster entities matching a set of ids.
     *
     * @param ids the monster ids, possibly null
     * @return the matching monsters
     * @throws ResourceNotFoundException if one of the ids does not exist
     */
    private Set<Monster> loadMonsters(Set<Long> ids) {
        Set<Monster> monsters = new HashSet<>();
        if (ids != null) {
            for (Long monsterId : ids) {
                monsters.add(monsterService.getEntityOrThrow(monsterId));
            }
        }
        return monsters;
    }

    /**
     * Checks that a group of monsters contains at most one boss.
     *
     * @param monsters the monsters that would live in the dungeon
     * @throws BusinessRuleException if more than one of them is a boss
     */
    private void checkAtMostOneBoss(Collection<Monster> monsters) {
        long bosses = monsters.stream().filter(m -> m.getMonsterType() == MonsterType.BOSS).count();
        if (bosses > 1) {
            throw new BusinessRuleException("monsterIds", "A dungeon can contain at most one boss.");
        }
    }

    /**
     * Computes the threat score of a dungeon entity.
     *
     * @param dungeon the dungeon to evaluate
     * @return the sum of the monster scores, with the boss counting three times
     */
    private int computeThreatScore(Dungeon dungeon) {
        int total = 0;
        for (Monster monster : dungeon.getMonsters()) {
            int score = monster.getHealth() / 10 + monster.getDamage();
            if(monster.getMonsterType() == MonsterType.BOSS) {
                total += score * 3;
            } 
            else if(monster.getMonsterType() == MonsterType.ELITE) {
                total += score * 1.5;            
            }
            else {
                total += score;
            }
        }
        return total;
    }

    /**
     * Converts a dungeon entity into its response, with its monsters sorted by name and its threat score.
     * Must be called while the transaction is open, because it reads the monsters.
     *
     * @param dungeon the entity to convert
     * @return the matching response object
     */
    private DungeonResponse toResponse(Dungeon dungeon) {
        List<MonsterResponse> monsters = dungeon.getMonsters().stream()
                .sorted(Comparator.comparing(Monster::getName, String.CASE_INSENSITIVE_ORDER))
                .map(MonsterResponse::from)
                .toList();
        return DungeonResponse.from(dungeon, monsters, computeThreatScore(dungeon));
    }
}