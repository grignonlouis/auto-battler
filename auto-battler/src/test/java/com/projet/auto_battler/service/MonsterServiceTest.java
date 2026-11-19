package com.projet.auto_battler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.projet.auto_battler.entity.Monster;
import com.projet.auto_battler.entity.MonsterType;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.MonsterRepository;
import com.projet.auto_battler.service.dto.MonsterRequest;
import com.projet.auto_battler.service.dto.MonsterResponse;

@ExtendWith(MockitoExtension.class)
class MonsterServiceTest {

    @Mock
    private MonsterRepository monsterRepository;

    @InjectMocks
    private MonsterService monsterService;

    private MonsterRequest request(String name) {
        MonsterRequest request = new MonsterRequest();
        request.setName(name);
        request.setMonsterType(MonsterType.values()[0]);
        request.setHealth(50);
        request.setDamage(8);
        request.setDefense(2);
        return request;
    }

    private Monster monster(Long id, String name) {
        Monster monster = new Monster();
        monster.setId(id);
        monster.setName(name);
        monster.setMonsterType(MonsterType.values()[0]);
        monster.setHealth(50);
        return monster;
    }

    @Test
    void createSavesMonsterWhenNameIsFree() {
        when(monsterRepository.existsByNameIgnoreCase("Goblin")).thenReturn(false);
        when(monsterRepository.save(any(Monster.class))).thenAnswer(call -> {
            Monster saved = call.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        MonsterResponse response = monsterService.create(request("  Goblin  "));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Goblin");
    }

    @Test
    void createFailsWhenNameAlreadyExists() {
        when(monsterRepository.existsByNameIgnoreCase("Goblin")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> monsterService.create(request("Goblin")));

        verify(monsterRepository, never()).save(any());
    }

    @Test
    void updateAllowsKeepingOwnName() {
        when(monsterRepository.findById(1L)).thenReturn(Optional.of(monster(1L, "Goblin")));
        when(monsterRepository.existsByNameIgnoreCaseAndIdNot("Goblin", 1L)).thenReturn(false);
        when(monsterRepository.save(any(Monster.class))).thenAnswer(call -> call.getArgument(0));

        MonsterResponse response = monsterService.update(1L, request("Goblin"));

        assertThat(response.name()).isEqualTo("Goblin");
    }

    @Test
    void updateFailsWhenNameBelongsToAnotherMonster() {
        when(monsterRepository.findById(1L)).thenReturn(Optional.of(monster(1L, "Goblin")));
        when(monsterRepository.existsByNameIgnoreCaseAndIdNot("Orc", 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> monsterService.update(1L, request("Orc")));

        verify(monsterRepository, never()).save(any());
    }

    @Test
    void findByIdFailsWhenMonsterDoesNotExist() {
        when(monsterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> monsterService.findById(99L));
    }

    @Test
    void deleteFailsWhenMonsterDoesNotExist() {
        when(monsterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> monsterService.delete(99L));

        verify(monsterRepository, never()).delete(any());
    }

    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(monsterRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(monster(1L, "Goblin"), monster(2L, "Orc")));

        List<MonsterResponse> result = monsterService.findAll(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllWithSearchFiltersByName() {
        when(monsterRepository.findByNameContainingIgnoreCase(any(String.class), any(Sort.class)))
                .thenReturn(List.of(monster(1L, "Goblin")));

        List<MonsterResponse> result = monsterService.findAll("gob");

        assertThat(result).extracting(MonsterResponse::name).containsExactly("Goblin");
    }
}