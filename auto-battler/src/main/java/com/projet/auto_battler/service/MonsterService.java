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

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.repository.DungeonRepository;

@Service
@Transactional
public class MonsterService {

    private final MonsterRepository monsterRepository;
    private final DungeonRepository dungeonRepository;

    public MonsterService(MonsterRepository monsterRepository, DungeonRepository dungeonRepository) {
        this.monsterRepository = monsterRepository;
        this.dungeonRepository = dungeonRepository;
    }

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

    @Transactional(readOnly = true)
    public MonsterResponse findById(Long id) {
        return MonsterResponse.from(getEntityOrThrow(id));
    }

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
        return request;
    }

    public MonsterResponse create(MonsterRequest request) {
        String name = request.getName().trim();
        if (monsterRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("A monster named \"" + name + "\" already exists.");
        }
        Monster monster = new Monster();
        copyToEntity(request, monster);
        return MonsterResponse.from(monsterRepository.save(monster));
    }

    public MonsterResponse update(Long id, MonsterRequest request) {
        Monster monster = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (monsterRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("A monster named \"" + name + "\" already exists.");
        }
        copyToEntity(request, monster);
        return MonsterResponse.from(monsterRepository.save(monster));
    }

    public void delete(Long id) {
        Monster monster = getEntityOrThrow(id);
        // Dungeon owns the join table, so a linked monster must be removed or the delete fails.
        for (Dungeon dungeon : dungeonRepository.findByMonstersId(id)) {
            dungeon.getMonsters().removeIf(existing -> existing.getId().equals(id));
        }
        monsterRepository.delete(monster);
    }

    @Transactional(readOnly = true)
    public Monster getEntityOrThrow(Long id) {
        return monsterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Monster", id));
    }

    private void copyToEntity(MonsterRequest request, Monster monster) {
        monster.setName(request.getName().trim());
        monster.setDescription(request.getDescription());
        monster.setMonsterType(request.getMonsterType());
        monster.setHealth(request.getHealth());
        monster.setDamage(request.getDamage());
        monster.setDefense(request.getDefense());
    }
}