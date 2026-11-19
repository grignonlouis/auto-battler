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
import com.projet.auto_battler.repository.PlayerRepository;
import com.projet.auto_battler.service.dto.DungeonRequest;
import com.projet.auto_battler.service.dto.DungeonResponse;
import com.projet.auto_battler.service.dto.MonsterResponse;

@Service
@Transactional
public class DungeonService {

    private final DungeonRepository dungeonRepository;
    private final MonsterService monsterService;
    private final PlayerRepository playerRepository;

    public DungeonService(DungeonRepository dungeonRepository,
                          MonsterService monsterService,
                          PlayerRepository playerRepository) {
        this.dungeonRepository = dungeonRepository;
        this.monsterService = monsterService;
        this.playerRepository = playerRepository;
    }

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

    @Transactional(readOnly = true)
    public DungeonResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

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

    public DungeonResponse create(DungeonRequest request) {
        String name = request.getName().trim();
        if (dungeonRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("name", "A dungeon named \"" + name + "\" already exists.");
        }
        Dungeon dungeon = new Dungeon();
        copyToEntity(request, dungeon);
        return toResponse(dungeonRepository.save(dungeon));
    }

    public DungeonResponse update(Long id, DungeonRequest request) {
        Dungeon dungeon = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (dungeonRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("name", "A dungeon named \"" + name + "\" already exists.");
        }
        copyToEntity(request, dungeon);
        return toResponse(dungeonRepository.save(dungeon));
    }

    public void delete(Long id) {
        // Monsters are kept. A profile that reached this dungeon blocks the delete.
        Dungeon dungeon = getEntityOrThrow(id);
        if (playerRepository.existsByProfile_HighestDungeonReached_Id(id)) {
            throw new BusinessRuleException(
                    "This dungeon is a player's highest reached dungeon and cannot be deleted.");
        }
        dungeonRepository.delete(dungeon);
    }

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

    public DungeonResponse removeMonster(Long dungeonId, Long monsterId) {
        Dungeon dungeon = getEntityOrThrow(dungeonId);
        monsterService.getEntityOrThrow(monsterId);
        dungeon.getMonsters().removeIf(existing -> existing.getId().equals(monsterId));
        return toResponse(dungeon);
    }

    @Transactional(readOnly = true)
    public int threatScore(Long id) {
        return computeThreatScore(getEntityOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Dungeon getEntityOrThrow(Long id) {
        return dungeonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dungeon", id));
    }

    private void copyToEntity(DungeonRequest request, Dungeon dungeon) {
        // Reject invalid monsters before writing, so a failed request leaves the dungeon unchanged.
        Set<Monster> monsters = loadMonsters(request.getMonsterIds());
        checkAtMostOneBoss(monsters);
        dungeon.setName(request.getName().trim());
        dungeon.setDescription(request.getDescription().trim());
        dungeon.setDifficulty(request.getDifficulty());
        dungeon.setUnlocked(request.getUnlocked());
        dungeon.getMonsters().clear();
        dungeon.getMonsters().addAll(monsters);
    }

    private Set<Monster> loadMonsters(Set<Long> ids) {
        Set<Monster> monsters = new HashSet<>();
        if (ids != null) {
            for (Long monsterId : ids) {
                monsters.add(monsterService.getEntityOrThrow(monsterId));
            }
        }
        return monsters;
    }

    private void checkAtMostOneBoss(Collection<Monster> monsters) {
        long bosses = monsters.stream().filter(m -> m.getMonsterType() == MonsterType.BOSS).count();
        if (bosses > 1) {
            throw new BusinessRuleException("monsterIds", "A dungeon can contain at most one boss.");
        }
    }

    private int computeThreatScore(Dungeon dungeon) {
        // health / 10 + damage. An elite counts twice, a boss three times.
        int total = 0;
        for (Monster monster : dungeon.getMonsters()) {
            int score = monster.getHealth() / 10 + monster.getDamage();
            if (monster.getMonsterType() == MonsterType.BOSS) {
                total += score * 3;
            } else if (monster.getMonsterType() == MonsterType.ELITE) {
                total += score * 2;
            } else {
                total += score;
            }
        }

        return total;
    }

    private DungeonResponse toResponse(Dungeon dungeon) {
        List<MonsterResponse> monsters = dungeon.getMonsters().stream()
                .sorted(Comparator.comparing(Monster::getName, String.CASE_INSENSITIVE_ORDER))
                .map(MonsterResponse::from)
                .toList();
        return DungeonResponse.from(dungeon, monsters, computeThreatScore(dungeon));
    }
}