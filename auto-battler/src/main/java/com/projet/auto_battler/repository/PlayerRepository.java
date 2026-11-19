package com.projet.auto_battler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @EntityGraph(attributePaths = {"profile", "profile.highestDungeonReached"})
    Optional<Player> findByEmailIgnoreCase(String email);

    @Override
    @EntityGraph(attributePaths = {"profile", "profile.highestDungeonReached"})
    Optional<Player> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"profile", "profile.highestDungeonReached"})
    List<Player> findAll(Sort sort);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByProfile_HighestDungeonReached_Id(Long dungeonId);

    @EntityGraph(attributePaths = {"profile", "profile.highestDungeonReached"})
    List<Player> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Sort sort);
}
