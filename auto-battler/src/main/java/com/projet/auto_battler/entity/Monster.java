package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class Monster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private MonsterType type;
    @Column
    private int health;
    @Column
    private int attack;
    @Column
    private int defense;
    @Column
    private int speed;
    @Column
    private boolean isBoss;
}
