package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class BattleLogEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int turn;
    @Column
    private String actorName;
    @Column
    private int entryType;
    @Column
    private String message;
}
