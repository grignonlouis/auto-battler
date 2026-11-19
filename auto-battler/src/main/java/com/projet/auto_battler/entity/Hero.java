package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class Hero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Role role;
    @Column
    private int health;
    @Column
    private int attackDamage;
    @Column
    private int defense;
    @Column
    private int speed;
    @Column
    private int level;
    @Column
    private int experience;
    @Column
    private int unlockable;
    @Column 
    private int price;
    @ManyToMany
    private java.util.List<Equipment> equipments;
    @ManyToMany
    private java.util.List<Skill> skills;

}
