package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class PlayerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    int totalVictories;
    @Column
    int totalDefeats;
    @Column
    int gold;
    @ManyToOne
    @JoinColumn(name = "highest_dungeon_reached_id")
    Dungeon highestDungeonReached;
}
