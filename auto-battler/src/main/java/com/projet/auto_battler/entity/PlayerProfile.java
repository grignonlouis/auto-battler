package com.projet.auto_battler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PlayerProfile {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Player player;

    @Column(nullable = false)
    private int totalVictories;

    @Column(nullable = false)
    private int totalDefeats;

    @Column(nullable = false)
    private int gold;

    @ManyToOne
    @JoinColumn(name = "highest_dungeon_reached_id")
    private Dungeon highestDungeonReached;
}
