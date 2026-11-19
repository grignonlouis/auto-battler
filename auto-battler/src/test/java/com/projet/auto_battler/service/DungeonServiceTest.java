package com.projet.auto_battler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.entity.Monster;
import com.projet.auto_battler.entity.MonsterType;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.DungeonRepository;
import com.projet.auto_battler.repository.PlayerRepository;
import com.projet.auto_battler.service.dto.DungeonRequest;
import com.projet.auto_battler.service.dto.DungeonResponse;

@ExtendWith(MockitoExtension.class)
class DungeonServiceTest {

    @Mock
    private DungeonRepository dungeonRepository;

    @Mock
    private MonsterService monsterService;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private DungeonService dungeonService;

    private DungeonRequest request(String name) {
        DungeonRequest request = new DungeonRequest();
        request.setName(name);
        request.setDescription("A dark place");
        request.setDifficulty(3);
        request.setUnlocked(true);
        return request;
    }

    private Dungeon dungeon(Long id, String name) {
        Dungeon dungeon = new Dungeon();
        dungeon.setId(id);
        dungeon.setName(name);
        dungeon.setDescription("A dark place");
        dungeon.setDifficulty(3);
        dungeon.setUnlocked(true);
        return dungeon;
    }

    private Monster monster(Long id, String name, int health, int damage, MonsterType type) {
        Monster monster = new Monster();
        monster.setId(id);
        monster.setName(name);
        monster.setHealth(health);
        monster.setDamage(damage);
        monster.setMonsterType(type);
        return monster;
    }

    @Test
    void createSavesDungeonWhenNameIsFree() {
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(dungeonRepository.save(any(Dungeon.class))).thenAnswer(call -> {
            Dungeon saved = call.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        DungeonResponse response = dungeonService.create(request("  Crypt  "));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Crypt");
    }

    @Test
    void createLinksTheSelectedMonsters() {
        Monster goblin = monster(10L, "Goblin", 100, 5, MonsterType.NORMAL);
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(10L));
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);
        when(dungeonRepository.save(any(Dungeon.class))).thenAnswer(call -> call.getArgument(0));

        DungeonResponse response = dungeonService.create(request);

        assertThat(response.monsters()).hasSize(1);
        assertThat(response.monsters().get(0).name()).isEqualTo("Goblin");
    }

    @Test
    void createFailsWhenAMonsterDoesNotExist() {
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(99L));
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(monsterService.getEntityOrThrow(99L)).thenThrow(new ResourceNotFoundException("Monster", 99L));

        assertThrows(ResourceNotFoundException.class, () -> dungeonService.create(request));

        verify(dungeonRepository, never()).save(any());
    }

    @Test
    void createFailsWhenTwoBossesAreSelected() {
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(10L, 11L));
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(monsterService.getEntityOrThrow(10L)).thenReturn(monster(10L, "Lich", 200, 10, MonsterType.BOSS));
        when(monsterService.getEntityOrThrow(11L)).thenReturn(monster(11L, "Dragon", 300, 15, MonsterType.BOSS));

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> dungeonService.create(request));

        assertThat(exception.getField()).isEqualTo("monsterIds");
        verify(dungeonRepository, never()).save(any());
    }

    @Test
    void createFailsWhenNameAlreadyExists() {
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> dungeonService.create(request("Crypt")));

        assertThat(exception.getField()).isEqualTo("name");
        verify(dungeonRepository, never()).save(any());
    }

    @Test
    void updateAllowsKeepingOwnName() {
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon(1L, "Crypt")));
        when(dungeonRepository.existsByNameIgnoreCaseAndIdNot("Crypt", 1L)).thenReturn(false);
        when(dungeonRepository.save(any(Dungeon.class))).thenAnswer(call -> call.getArgument(0));

        DungeonResponse response = dungeonService.update(1L, request("Crypt"));

        assertThat(response.name()).isEqualTo("Crypt");
    }

    @Test
    void updateFailsWhenNameBelongsToAnotherDungeon() {
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon(1L, "Crypt")));
        when(dungeonRepository.existsByNameIgnoreCaseAndIdNot("Forest", 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> dungeonService.update(1L, request("Forest")));

        verify(dungeonRepository, never()).save(any());
    }

    @Test
    void updateReplacesTheMonsters() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Goblin", 100, 5, MonsterType.NORMAL));
        Monster orc = monster(11L, "Orc", 150, 8, MonsterType.NORMAL);
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(11L));
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(dungeonRepository.existsByNameIgnoreCaseAndIdNot("Crypt", 1L)).thenReturn(false);
        when(monsterService.getEntityOrThrow(11L)).thenReturn(orc);
        when(dungeonRepository.save(any(Dungeon.class))).thenAnswer(call -> call.getArgument(0));

        DungeonResponse response = dungeonService.update(1L, request);

        assertThat(response.monsters()).extracting(monsterResponse -> monsterResponse.name())
                .containsExactly("Orc");
    }

    @Test
    void findByIdFailsWhenDungeonDoesNotExist() {
        when(dungeonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dungeonService.findById(99L));
    }

    @Test
    void deleteFailsWhenDungeonDoesNotExist() {
        when(dungeonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dungeonService.delete(99L));

        verify(dungeonRepository, never()).delete(any());
    }

    @Test
    void deleteFailsWhenAPlayerReachedTheDungeon() {
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon(1L, "Crypt")));
        when(playerRepository.existsByProfile_HighestDungeonReached_Id(1L)).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> dungeonService.delete(1L));

        assertThat(exception.getMessage()).contains("highest");
        verify(dungeonRepository, never()).delete(any());
    }

    @Test
    void deleteRemovesDungeonWhenNoPlayerReachedIt() {
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon(1L, "Crypt")));
        when(playerRepository.existsByProfile_HighestDungeonReached_Id(1L)).thenReturn(false);

        dungeonService.delete(1L);

        verify(dungeonRepository).delete(any(Dungeon.class));
    }

    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(dungeonRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(dungeon(1L, "Crypt"), dungeon(2L, "Forest")));

        List<DungeonResponse> result = dungeonService.findAll(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllWithSearchFiltersByName() {
        when(dungeonRepository.findByNameContainingIgnoreCase(any(String.class), any(Sort.class)))
                .thenReturn(List.of(dungeon(1L, "Crypt")));

        List<DungeonResponse> result = dungeonService.findAll("cry");

        assertThat(result).extracting(DungeonResponse::name).containsExactly("Crypt");
    }

    @Test
    void addMonsterAddsMonsterToDungeon() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        Monster goblin = monster(10L, "Goblin", 100, 5, MonsterType.NORMAL);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);

        dungeonService.addMonster(1L, 10L);

        assertThat(dungeon.getMonsters()).containsExactly(goblin);
    }

    @Test
    void addMonsterTwiceKeepsASingleCopy() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        Monster goblin = monster(10L, "Goblin", 100, 5, MonsterType.NORMAL);
        dungeon.getMonsters().add(goblin);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);

        dungeonService.addMonster(1L, 10L);

        assertThat(dungeon.getMonsters()).hasSize(1);
    }

    @Test
    void addMonsterFailsWhenDungeonAlreadyHasABoss() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Lich", 200, 10, MonsterType.BOSS));
        Monster secondBoss = monster(11L, "Dragon", 300, 15, MonsterType.BOSS);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(11L)).thenReturn(secondBoss);

        assertThrows(BusinessRuleException.class, () -> dungeonService.addMonster(1L, 11L));

        assertThat(dungeon.getMonsters()).doesNotContain(secondBoss);
    }

    @Test
    void addMonsterAllowsNormalMonsterWhenDungeonHasABoss() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Lich", 200, 10, MonsterType.BOSS));
        Monster goblin = monster(11L, "Goblin", 100, 5, MonsterType.NORMAL);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(11L)).thenReturn(goblin);

        dungeonService.addMonster(1L, 11L);

        assertThat(dungeon.getMonsters()).contains(goblin);
    }

    @Test
    void removeMonsterRemovesItFromDungeon() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        Monster goblin = monster(10L, "Goblin", 100, 5, MonsterType.NORMAL);
        dungeon.getMonsters().add(goblin);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);

        dungeonService.removeMonster(1L, 10L);

        assertThat(dungeon.getMonsters()).isEmpty();
    }

    @Test
    void threatScoreSumsMonstersAndTriplesTheBoss() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Goblin", 100, 5, MonsterType.NORMAL));
        dungeon.getMonsters().add(monster(11L, "Golem", 150, 15, MonsterType.ELITE));
        dungeon.getMonsters().add(monster(12L, "Lich", 200, 30, MonsterType.BOSS));
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));

        int score = dungeonService.threatScore(1L);

        assertThat(score).isEqualTo(225);
    }
}