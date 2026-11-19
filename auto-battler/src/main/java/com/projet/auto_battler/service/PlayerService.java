package com.projet.auto_battler.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.entity.PlayerProfile;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.PlayerRepository;
import com.projet.auto_battler.service.dto.PlayerRequest;
import com.projet.auto_battler.service.dto.PlayerResponse;
import com.projet.auto_battler.service.dto.RegisterDTO;

@Service
@Transactional
public class PlayerService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    private final PlayerRepository playerRepository;
    private final DungeonService dungeonService;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository,
                         DungeonService dungeonService,
                         PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.dungeonService = dungeonService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findAll(String search) {
        Sort sort = Sort.by("username");
        List<Player> players;
        if (search == null || search.isBlank()) {
            players = playerRepository.findAll(sort);
        } else {
            String term = search.trim();
            players = playerRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    term, term, sort);
        }
        return players.stream().map(PlayerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PlayerResponse findById(Long id) {
        return PlayerResponse.from(getEntityOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PlayerRequest getForEdit(Long id) {
        Player player = getEntityOrThrow(id);
        PlayerProfile profile = player.getProfile();
        PlayerRequest request = new PlayerRequest();
        request.setUsername(player.getUsername());
        request.setEmail(player.getEmail());
        request.setAdmin(player.isAdmin());
        request.setGold(profile == null ? 0 : profile.getGold());
        request.setTotalVictories(profile == null ? 0 : profile.getTotalVictories());
        request.setTotalDefeats(profile == null ? 0 : profile.getTotalDefeats());
        if (profile != null && profile.getHighestDungeonReached() != null) {
            request.setHighestDungeonId(profile.getHighestDungeonReached().getId());
        }
        return request;
    }

    // Public signup. Progression starts at zero and the account is not an admin.
    public Player register(RegisterDTO dto) {
        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())) {
            throw new BusinessRuleException("confirmPassword", "Password and confirmation do not match.");
        }
        PlayerRequest request = new PlayerRequest();
        request.setUsername(dto.getUsername());
        request.setEmail(dto.getEmail());
        request.setPassword(dto.getPassword());
        request.setGold(0);
        request.setTotalVictories(0);
        request.setTotalDefeats(0);
        request.setAdmin(false);
        Player player = new Player();
        copyToEntity(request, player, true);
        return playerRepository.save(player);
    }

    public PlayerResponse create(PlayerRequest request) {
        Player player = new Player();
        copyToEntity(request, player, true);
        return PlayerResponse.from(playerRepository.save(player));
    }

    public PlayerResponse update(Long id, PlayerRequest request) {
        Player player = getEntityOrThrow(id);
        copyToEntity(request, player, false);
        return PlayerResponse.from(playerRepository.save(player));
    }

    public void delete(Long id) {
        // The profile is removed with the account.
        Player player = getEntityOrThrow(id);
        playerRepository.delete(player);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayer(String email, String password) {
        if (email == null || email.isBlank() || password == null) {
            return Optional.empty();
        }
        return playerRepository.findByEmailIgnoreCase(email.trim())
                .filter(player -> passwordEncoder.matches(password, player.getPasswordHash()));
    }

    @Transactional(readOnly = true)
    public Player getEntityOrThrow(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    private void copyToEntity(PlayerRequest request, Player player, boolean creating) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();
        String encodedPassword = resolvePassword(request.getPassword(), creating);
        checkUnique(username, email, creating ? null : player.getId());
        Dungeon dungeon = null;
        if (request.getHighestDungeonId() != null) {
            dungeon = dungeonService.getEntityOrThrow(request.getHighestDungeonId());
        }

        player.setUsername(username);
        player.setEmail(email);
        player.setAdmin(Boolean.TRUE.equals(request.getAdmin()));
        if (encodedPassword != null) {
            player.setPasswordHash(encodedPassword);
        }
        PlayerProfile profile = profileOf(player);
        profile.setGold(request.getGold());
        profile.setTotalVictories(request.getTotalVictories());
        profile.setTotalDefeats(request.getTotalDefeats());
        profile.setHighestDungeonReached(dungeon);
    }

    private PlayerProfile profileOf(Player player) {
        if (player.getProfile() != null) {
            return player.getProfile();
        }
        PlayerProfile profile = new PlayerProfile();
        profile.setPlayer(player);
        player.setProfile(profile);
        return profile;
    }

    private String resolvePassword(String rawPassword, boolean creating) {
        if (rawPassword == null || rawPassword.isBlank()) {
            if (creating) {
                throw new BusinessRuleException("password", "Password is required.");
            }
            return null;
        }
        if (rawPassword.length() < MIN_PASSWORD_LENGTH || rawPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new BusinessRuleException("password", "Password must be between 8 and 100 characters.");
        }
        return passwordEncoder.encode(rawPassword);
    }

    private void checkUnique(String username, String email, Long currentId) {
        boolean usernameTaken = currentId == null
                ? playerRepository.existsByUsernameIgnoreCase(username)
                : playerRepository.existsByUsernameIgnoreCaseAndIdNot(username, currentId);
        if (usernameTaken) {
            throw new BusinessRuleException("username", "Username \"" + username + "\" is already taken.");
        }
        boolean emailTaken = currentId == null
                ? playerRepository.existsByEmailIgnoreCase(email)
                : playerRepository.existsByEmailIgnoreCaseAndIdNot(email, currentId);
        if (emailTaken) {
            throw new BusinessRuleException("email", "An account with this email already exists.");
        }
    }
}
