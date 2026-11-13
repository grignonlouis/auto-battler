package com.projet.auto_battler.entity;

import java.util.Date;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

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
