package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Stores a player's accumulated victories, defeats, gold and highest dungeon reached.
 *
 * <p>The highest dungeon is a many-to-one association, so several profiles may reference the same dungeon.
 */
@Data
@Entity
public class PlayerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int totalVictories;
    @Column
    private int totalDefeats;
    @Column
    private int gold;
    @ManyToOne
    @JoinColumn(name = "highest_dungeon_reached_id")
    private Dungeon highestDungeonReached;
}
