package com.projet.auto_battler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An enemy that heroes fight in dungeons.
 * A monster can be flagged as a boss. The relation with dungeons is owned by the Dungeon entity.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonsterType monsterType;

    private int health;

    private int damage;

    private int defense;

    private boolean boss;
}