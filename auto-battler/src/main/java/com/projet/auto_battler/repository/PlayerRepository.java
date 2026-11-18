package com.projet.auto_battler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projet.auto_battler.entity.Player;

import java.util.Optional;

/**
 * Spring Data access to player accounts.
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {

    /**
     * Looks up a player by email.
     *
     * @param email the email address to search for
     * @return the matching player, if one exists
     */
    Optional<Player> findByEmail(String email);

    /**
     * Finds an administrator using the repository's native SQL query.
     *
     * @return an administrator, if one exists
     */
    @Query (value = "SELECT * FROM players p WHERE p.isAdmin = true",nativeQuery = true)
    Optional<Player> searchAdmins();
}
