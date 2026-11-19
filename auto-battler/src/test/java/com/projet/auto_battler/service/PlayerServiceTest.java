package com.projet.auto_battler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.entity.PlayerProfile;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.PlayerRepository;
import com.projet.auto_battler.service.dto.PlayerRequest;
import com.projet.auto_battler.service.dto.PlayerResponse;
import com.projet.auto_battler.service.dto.RegisterDTO;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private DungeonService dungeonService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerService playerService;

    @Captor
    private ArgumentCaptor<Player> playerCaptor;

    private RegisterDTO registration() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("  Ada  ");
        dto.setEmail("  Ada@Example.com  ");
        dto.setPassword("secret123");
        dto.setConfirmPassword("secret123");
        return dto;
    }

    private PlayerRequest request(String username, String email) {
        PlayerRequest request = new PlayerRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword("secret123");
        request.setGold(10);
        request.setTotalVictories(1);
        request.setTotalDefeats(2);
        request.setAdmin(false);
        return request;
    }

    private Player player(Long id, String username, String email) {
        Player player = new Player();
        player.setId(id);
        player.setUsername(username);
        player.setEmail(email);
        player.setPasswordHash("hash");
        player.setAdmin(false);
        PlayerProfile profile = new PlayerProfile();
        profile.setId(id);
        profile.setPlayer(player);
        profile.setGold(5);
        profile.setTotalVictories(2);
        profile.setTotalDefeats(1);
        player.setProfile(profile);
        return player;
    }

    private void stubSaveAssignsId() {
        when(playerRepository.save(any(Player.class))).thenAnswer(call -> {
            Player saved = call.getArgument(0);
            saved.setId(7L);
            return saved;
        });
    }

    @Test
    void registerSavesAccountWithZeroedProfile() {
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        stubSaveAssignsId();

        Player saved = playerService.register(registration());

        assertThat(saved.getId()).isEqualTo(7L);
        assertThat(saved.getUsername()).isEqualTo("Ada");
        assertThat(saved.getEmail()).isEqualTo("Ada@Example.com");
        assertThat(saved.getPasswordHash()).isEqualTo("hash");
        assertThat(saved.isAdmin()).isFalse();
        assertThat(saved.getProfile().getPlayer()).isSameAs(saved);
        assertThat(saved.getProfile().getGold()).isZero();
        assertThat(saved.getProfile().getTotalVictories()).isZero();
        assertThat(saved.getProfile().getTotalDefeats()).isZero();
        assertThat(saved.getProfile().getHighestDungeonReached()).isNull();
    }

    @Test
    void registerFailsWhenPasswordsDiffer() {
        RegisterDTO dto = registration();
        dto.setConfirmPassword("other-password");

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> playerService.register(dto));

        assertThat(exception.getField()).isEqualTo("confirmPassword");
        verify(playerRepository, never()).save(any());
    }

    @Test
    void registerFailsWhenUsernameIsTaken() {
        when(playerRepository.existsByUsernameIgnoreCase("Ada")).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> playerService.register(registration()));

        assertThat(exception.getField()).isEqualTo("username");
        verify(playerRepository, never()).save(any());
    }

    @Test
    void registerFailsWhenEmailIsTaken() {
        when(playerRepository.existsByEmailIgnoreCase("Ada@Example.com")).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> playerService.register(registration()));

        assertThat(exception.getField()).isEqualTo("email");
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createRequiresAPassword() {
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setPassword("  ");

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> playerService.create(request));

        assertThat(exception.getField()).isEqualTo("password");
        verify(playerRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void createRejectsAShortPassword() {
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setPassword("short");

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> playerService.create(request));

        assertThat(exception.getField()).isEqualTo("password");
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createLinksTheHighestDungeon() {
        Dungeon crypt = new Dungeon();
        crypt.setId(3L);
        crypt.setName("Crypt");
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setHighestDungeonId(3L);
        request.setAdmin(true);
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        when(dungeonService.getEntityOrThrow(3L)).thenReturn(crypt);
        stubSaveAssignsId();

        PlayerResponse response = playerService.create(request);

        assertThat(response.admin()).isTrue();
        assertThat(response.highestDungeonId()).isEqualTo(3L);
        assertThat(response.highestDungeonName()).isEqualTo("Crypt");
        assertThat(response.gold()).isEqualTo(10);
    }

    @Test
    void createFailsWhenTheDungeonDoesNotExist() {
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setHighestDungeonId(99L);
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        when(dungeonService.getEntityOrThrow(99L)).thenThrow(new ResourceNotFoundException("Dungeon", 99L));

        assertThrows(ResourceNotFoundException.class, () -> playerService.create(request));

        verify(playerRepository, never()).save(any());
    }

    @Test
    void updateKeepsThePasswordWhenBlank() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player(1L, "Ada", "ada@example.com")));
        when(playerRepository.save(any(Player.class))).thenAnswer(call -> call.getArgument(0));
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setPassword(null);
        request.setGold(40);

        PlayerResponse response = playerService.update(1L, request);

        assertThat(response.gold()).isEqualTo(40);
        verify(passwordEncoder, never()).encode(any());
        verify(playerRepository).save(playerCaptor.capture());
        assertThat(playerCaptor.getValue().getPasswordHash()).isEqualTo("hash");
    }

    @Test
    void updateChangesThePasswordWhenProvided() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player(1L, "Ada", "ada@example.com")));
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");
        when(playerRepository.save(any(Player.class))).thenAnswer(call -> call.getArgument(0));
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setPassword("new-secret");

        playerService.update(1L, request);

        verify(playerRepository).save(playerCaptor.capture());
        assertThat(playerCaptor.getValue().getPasswordHash()).isEqualTo("new-hash");
    }

    @Test
    void updateAllowsKeepingOwnEmail() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player(1L, "Ada", "ada@example.com")));
        when(playerRepository.existsByEmailIgnoreCaseAndIdNot("ada@example.com", 1L)).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenAnswer(call -> call.getArgument(0));
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setPassword(null);

        PlayerResponse response = playerService.update(1L, request);

        assertThat(response.email()).isEqualTo("ada@example.com");
    }

    @Test
    void updateFailsWhenEmailBelongsToAnotherPlayer() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player(1L, "Ada", "ada@example.com")));
        when(playerRepository.existsByEmailIgnoreCaseAndIdNot("other@example.com", 1L)).thenReturn(true);
        PlayerRequest request = request("Ada", "other@example.com");
        request.setPassword(null);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> playerService.update(1L, request));

        assertThat(exception.getField()).isEqualTo("email");
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updateClearsTheHighestDungeon() {
        Dungeon crypt = new Dungeon();
        crypt.setId(3L);
        crypt.setName("Crypt");
        Player player = player(1L, "Ada", "ada@example.com");
        player.getProfile().setHighestDungeonReached(crypt);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenAnswer(call -> call.getArgument(0));
        PlayerRequest request = request("Ada", "ada@example.com");
        request.setPassword(null);

        PlayerResponse response = playerService.update(1L, request);

        assertThat(response.highestDungeonId()).isNull();
        assertThat(response.highestDungeonName()).isNull();
    }

    @Test
    void getForEditDoesNotCopyThePassword() {
        Dungeon crypt = new Dungeon();
        crypt.setId(3L);
        crypt.setName("Crypt");
        Player player = player(1L, "Ada", "ada@example.com");
        player.getProfile().setHighestDungeonReached(crypt);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerRequest request = playerService.getForEdit(1L);

        assertThat(request.getPassword()).isNull();
        assertThat(request.getUsername()).isEqualTo("Ada");
        assertThat(request.getGold()).isEqualTo(5);
        assertThat(request.getHighestDungeonId()).isEqualTo(3L);
    }

    @Test
    void findByIdFailsWhenPlayerDoesNotExist() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> playerService.findById(99L));
    }

    @Test
    void deleteFailsWhenPlayerDoesNotExist() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> playerService.delete(99L));

        verify(playerRepository, never()).delete(any());
    }

    @Test
    void deleteRemovesThePlayer() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player(1L, "Ada", "ada@example.com")));

        playerService.delete(1L);

        verify(playerRepository).delete(any(Player.class));
    }

    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(playerRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(player(1L, "Ada", "ada@example.com"), player(2L, "Bea", "bea@example.com")));

        List<PlayerResponse> result = playerService.findAll(null);

        assertThat(result).extracting(PlayerResponse::username).containsExactly("Ada", "Bea");
    }

    @Test
    void findAllWithSearchUsesUsernameAndEmail() {
        when(playerRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                eq("ada"), eq("ada"), any(Sort.class)))
                .thenReturn(List.of(player(1L, "Ada", "ada@example.com")));

        List<PlayerResponse> result = playerService.findAll("  ada  ");

        assertThat(result).extracting(PlayerResponse::email).containsExactly("ada@example.com");
    }

    @Test
    void findPlayerAcceptsTheMatchingPassword() {
        Player player = player(1L, "Ada", "ada@example.com");
        when(playerRepository.findByEmailIgnoreCase("ada@example.com")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("secret123", "hash")).thenReturn(true);

        Optional<Player> found = playerService.findPlayer("  ada@example.com  ", "secret123");

        assertThat(found).contains(player);
    }

    @Test
    void findPlayerRejectsAnUnknownEmailAndAWrongPasswordTheSameWay() {
        when(playerRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());
        Player player = player(1L, "Ada", "ada@example.com");
        when(playerRepository.findByEmailIgnoreCase("ada@example.com")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThat(playerService.findPlayer("missing@example.com", "secret123")).isEmpty();
        assertThat(playerService.findPlayer("ada@example.com", "wrong")).isEmpty();
    }
}
