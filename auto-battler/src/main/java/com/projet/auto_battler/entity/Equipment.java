package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Slot slot;
    @Column
    private int rarity;
    @Column
    private int bonusAttack;
    @Column
    private int bonusHealth;
    @Column
    private int bonusDefense;
    @Column
    private int bonusSpeed;

}
