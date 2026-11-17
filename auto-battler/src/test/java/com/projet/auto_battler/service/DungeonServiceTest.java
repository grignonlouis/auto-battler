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
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.DungeonRepository;
import com.projet.auto_battler.service.dto.DungeonRequest;
import com.projet.auto_battler.service.dto.DungeonResponse;

/**
 * Unit tests for DungeonService.
 * The repository and the monster service are mocked, so no Spring context and no database are involved.
 */
@ExtendWith(MockitoExtension.class)
class DungeonServiceTest {

    @Mock
    private DungeonRepository dungeonRepository;

    @Mock
    private MonsterService monsterService;

    @InjectMocks
    private DungeonService dungeonService;

    /**
     * Builds a valid request without monsters, used as a starting point by the tests.
     *
     * @param name the dungeon name
     * @return a request with default values
     */
    private DungeonRequest request(String name) {
        DungeonRequest request = new DungeonRequest();
        request.setName(name);
        request.setDescription("A dark place");
        request.setDifficulty(3);
        request.setUnlocked(true);
        return request;
    }

    /**
     * Builds a dungeon with the given id and name, without any monster.
     *
     * @param id   the dungeon id
     * @param name the dungeon name
     * @return a dungeon entity
     */
    private Dungeon dungeon(Long id, String name) {
        Dungeon dungeon = new Dungeon();
        dungeon.setId(id);
        dungeon.setName(name);
        dungeon.setDescription("A dark place");
        dungeon.setDifficulty(3);
        dungeon.setUnlocked(true);
        return dungeon;
    }

    /**
     * Builds a monster with the given id, stats and boss flag.
     *
     * @param id     the monster id
     * @param name   the monster name
     * @param boss   true if the monster is a boss
     * @param health the monster health
     * @param damage the monster damage
     * @return a monster entity
     */
    private Monster monster(Long id, String name, boolean boss, int health, int damage) {
        Monster monster = new Monster();
        monster.setId(id);
        monster.setName(name);
        monster.setBoss(boss);
        monster.setHealth(health);
        monster.setDamage(damage);
        return monster;
    }

    /**
     * A valid creation saves the dungeon and returns it with the trimmed name.
     */
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

    /**
     * Creating a dungeon with selected monsters links them to the dungeon.
     */
    @Test
    void createLinksTheSelectedMonsters() {
        Monster goblin = monster(10L, "Goblin", false, 100, 5);
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(10L));
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);
        when(dungeonRepository.save(any(Dungeon.class))).thenAnswer(call -> call.getArgument(0));

        DungeonResponse response = dungeonService.create(request);

        assertThat(response.monsters()).hasSize(1);
        assertThat(response.monsters().get(0).name()).isEqualTo("Goblin");
    }

    /**
     * Creating a dungeon with an unknown monster id fails and nothing is saved.
     */
    @Test
    void createFailsWhenAMonsterDoesNotExist() {
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(99L));
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(monsterService.getEntityOrThrow(99L)).thenThrow(new ResourceNotFoundException("Monster", 99L));

        assertThrows(ResourceNotFoundException.class, () -> dungeonService.create(request));

        verify(dungeonRepository, never()).save(any());
    }

    /**
     * Creating a dungeon with two bosses is refused, and the error points at the monsters field.
     */
    @Test
    void createFailsWhenTwoBossesAreSelected() {
        DungeonRequest request = request("Crypt");
        request.setMonsterIds(Set.of(10L, 11L));
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(false);
        when(monsterService.getEntityOrThrow(10L)).thenReturn(monster(10L, "Lich", true, 200, 10));
        when(monsterService.getEntityOrThrow(11L)).thenReturn(monster(11L, "Dragon", true, 300, 15));

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> dungeonService.create(request));

        assertThat(exception.getField()).isEqualTo("monsterIds");
        verify(dungeonRepository, never()).save(any());
    }

    /**
     * Creating a dungeon with a name that already exists is refused and nothing is saved.
     */
    @Test
    void createFailsWhenNameAlreadyExists() {
        when(dungeonRepository.existsByNameIgnoreCase("Crypt")).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> dungeonService.create(request("Crypt")));

        assertThat(exception.getField()).isEqualTo("name");
        verify(dungeonRepository, never()).save(any());
    }

    /**
     * A dungeon can be updated while keeping its own name.
     */
    @Test
    void updateAllowsKeepingOwnName() {
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon(1L, "Crypt")));
        when(dungeonRepository.existsByNameIgnoreCaseAndIdNot("Crypt", 1L)).thenReturn(false);
        when(dungeonRepository.save(any(Dungeon.class))).thenAnswer(call -> call.getArgument(0));

        DungeonResponse response = dungeonService.update(1L, request("Crypt"));

        assertThat(response.name()).isEqualTo("Crypt");
    }

    /**
     * Updating a dungeon with the name of another dungeon is refused.
     */
    @Test
    void updateFailsWhenNameBelongsToAnotherDungeon() {
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon(1L, "Crypt")));
        when(dungeonRepository.existsByNameIgnoreCaseAndIdNot("Forest", 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> dungeonService.update(1L, request("Forest")));

        verify(dungeonRepository, never()).save(any());
    }

    /**
     * Updating a dungeon replaces its monsters by the selected ones.
     */
    @Test
    void updateReplacesTheMonsters() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Goblin", false, 100, 5));
        Monster orc = monster(11L, "Orc", false, 150, 8);
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

    /**
     * Looking up an unknown id raises a not-found error.
     */
    @Test
    void findByIdFailsWhenDungeonDoesNotExist() {
        when(dungeonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dungeonService.findById(99L));
    }

    /**
     * Deleting an unknown id raises a not-found error and deletes nothing.
     */
    @Test
    void deleteFailsWhenDungeonDoesNotExist() {
        when(dungeonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dungeonService.delete(99L));

        verify(dungeonRepository, never()).delete(any());
    }

    /**
     * Without a search text, every dungeon is returned.
     */
    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(dungeonRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(dungeon(1L, "Crypt"), dungeon(2L, "Forest")));

        List<DungeonResponse> result = dungeonService.findAll(null);

        assertThat(result).hasSize(2);
    }

    /**
     * With a search text, only the repository search result is returned.
     */
    @Test
    void findAllWithSearchFiltersByName() {
        when(dungeonRepository.findByNameContainingIgnoreCase(any(String.class), any(Sort.class)))
                .thenReturn(List.of(dungeon(1L, "Crypt")));

        List<DungeonResponse> result = dungeonService.findAll("cry");

        assertThat(result).extracting(DungeonResponse::name).containsExactly("Crypt");
    }

    /**
     * Adding a monster to a dungeon puts it in the dungeon monster set.
     */
    @Test
    void addMonsterAddsMonsterToDungeon() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        Monster goblin = monster(10L, "Goblin", false, 100, 5);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);

        dungeonService.addMonster(1L, 10L);

        assertThat(dungeon.getMonsters()).containsExactly(goblin);
    }

    /**
     * Adding a monster that is already in the dungeon changes nothing.
     */
    @Test
    void addMonsterTwiceKeepsASingleCopy() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        Monster goblin = monster(10L, "Goblin", false, 100, 5);
        dungeon.getMonsters().add(goblin);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);

        dungeonService.addMonster(1L, 10L);

        assertThat(dungeon.getMonsters()).hasSize(1);
    }

    /**
     * A second boss cannot be added to a dungeon that already has one.
     */
    @Test
    void addMonsterFailsWhenDungeonAlreadyHasABoss() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Lich", true, 200, 10));
        Monster secondBoss = monster(11L, "Dragon", true, 300, 15);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(11L)).thenReturn(secondBoss);

        assertThrows(BusinessRuleException.class, () -> dungeonService.addMonster(1L, 11L));

        assertThat(dungeon.getMonsters()).doesNotContain(secondBoss);
    }

    /**
     * A normal monster can still be added to a dungeon that already has a boss.
     */
    @Test
    void addMonsterAllowsNormalMonsterWhenDungeonHasABoss() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Lich", true, 200, 10));
        Monster goblin = monster(11L, "Goblin", false, 100, 5);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(11L)).thenReturn(goblin);

        dungeonService.addMonster(1L, 11L);

        assertThat(dungeon.getMonsters()).contains(goblin);
    }

    /**
     * Removing a monster takes it out of the dungeon monster set.
     */
    @Test
    void removeMonsterRemovesItFromDungeon() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        Monster goblin = monster(10L, "Goblin", false, 100, 5);
        dungeon.getMonsters().add(goblin);
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));
        when(monsterService.getEntityOrThrow(10L)).thenReturn(goblin);

        dungeonService.removeMonster(1L, 10L);

        assertThat(dungeon.getMonsters()).isEmpty();
    }

    /**
     * The threat score adds up health / 10 + damagefor each monster, and the boss counts three times.
     * Goblin: 100 / 10 + 5 = 15. Lich (boss): ((200 / 10 ) + 10) x 3 = 90. Total: 105.
     */
    @Test
    void threatScoreSumsMonstersAndTriplesTheBoss() {
        Dungeon dungeon = dungeon(1L, "Crypt");
        dungeon.getMonsters().add(monster(10L, "Goblin", false, 100, 5));
        dungeon.getMonsters().add(monster(11L, "Lich", true, 200, 10));
        when(dungeonRepository.findById(1L)).thenReturn(Optional.of(dungeon));

        int score = dungeonService.threatScore(1L);

        assertThat(score).isEqualTo(105);
    }
}