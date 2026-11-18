package com.projet.auto_battler.service;

import java.util.Optional;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projet.auto_battler.service.dto.RegisterDTO;
import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.entity.PlayerProfile;
import com.projet.auto_battler.repository.PlayerProfileRepository;
import com.projet.auto_battler.repository.PlayerRepository;

/**
 * Handles player registration and credential lookup for the web login flow.
 */
@Service 
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates the service with its persistence and password-encoding dependencies.
     *
     * @param playerRepository repository for player accounts
     * @param profileRepository repository for player profiles
     * @param passwordEncoder encoder used to hash and verify passwords
     */
    public PlayerService(PlayerRepository playerRepository, PlayerProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Verifies the password confirmation, encodes the password and persists a player and a profile.
     *
     * @param userDTO submitted registration data
     * @return the persisted player account
     * @throws IllegalArgumentException if the password and confirmation differ
     */
    public Player createUser(RegisterDTO userDTO) {

        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            throw new IllegalArgumentException("password and confirmation password do not match");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        Player player = new Player(userDTO.getUsername(), userDTO.getEmail(), encodedPassword);
        playerRepository.save(player);

        PlayerProfile userProfile = new PlayerProfile();
        profileRepository.save(userProfile);

        return player;
    }

    /**
     * Looks up an account by email and verifies the supplied password against its stored hash.
     *
     * @param email account email address
     * @param password raw password supplied during login
     * @return the matching player when the email and password are valid; otherwise empty
     */
    public Optional<Player> findPlayer(String email, String password) {
        Optional<Player> dbplayer = playerRepository.findByEmail(email);
        if(dbplayer.isPresent()){
            Player player = dbplayer.get();
            if(passwordEncoder.matches(password, player.getPasswordHash())){
                return dbplayer;
            }
        }
        return Optional.empty();
    }
}
