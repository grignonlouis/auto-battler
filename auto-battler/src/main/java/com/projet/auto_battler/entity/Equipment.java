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
    Long id;
    @Column
    String name;
    @Column
    String description;
    @Column
    Slot slot;
    @Column
    int rarity;
    @Column
    int bonusAttack;
    @Column
    int bonusHealth;
    @Column
    int bonusDefense;
    @Column
    int bonusSpeed;

}
