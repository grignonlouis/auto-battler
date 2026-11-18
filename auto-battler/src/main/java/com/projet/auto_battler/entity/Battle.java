package com.projet.auto_battler.entity;

import java.util.Date;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Persists the dates, status and rewards recorded for a battle.
 *
 * <p>This entity currently has no JPA association to a team, dungeon or battle log.
 */
@Data
@Entity
public class Battle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Date startedAt;
    @Column
    private Date endedAt;
    @Column
    private Status status;
    @Column
    private int seed;
    @Column
    private int turnsPlayed;
    @Column
    private int xpGained;
    @Column
    private int goldGained;
}
