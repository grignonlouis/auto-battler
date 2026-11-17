package com.projet.auto_battler.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A dungeon that heroes can explore, filled with monsters.
 * Dungeon owns the many-to-many relation with Monster: the join table is dungeon_monster,
 * and linking or unlinking a monster means changing the monsters set of the dungeon.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Dungeon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private int difficulty;

    @Column(nullable = false)
    private boolean unlocked;

    @ManyToMany
    @JoinTable(
            name = "dungeon_monster",
            joinColumns = @JoinColumn(name = "dungeon_id"),
            inverseJoinColumns = @JoinColumn(name = "monster_id"))
    private Set<Monster> monsters = new HashSet<>();
}