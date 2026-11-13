package com.projet.auto_battler.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.auto_battler.entity.PlayerProfile;


public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {

}
