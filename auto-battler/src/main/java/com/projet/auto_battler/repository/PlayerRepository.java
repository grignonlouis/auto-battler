package com.projet.auto_battler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projet.auto_battler.entity.Player;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByEmail(String email);

    @Query (value = "SELECT * FROM players p WHERE p.isAdmin = true",nativeQuery = true)
    Optional<Player> searchAdmins();
}
