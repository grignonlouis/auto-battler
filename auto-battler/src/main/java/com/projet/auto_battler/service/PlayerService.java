package com.projet.auto_battler.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projet.auto_battler.service.dto.RegisterDTO;
import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.repository.PlayerRepository;

@Service 
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player createUser(RegisterDTO userDTO) {

        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            throw new IllegalArgumentException("password and confirmation password do not match");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        Player player = new Player(userDTO.getUsername(), userDTO.getEmail(), encodedPassword);
        playerRepository.save(player);

        return player;
    }

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
