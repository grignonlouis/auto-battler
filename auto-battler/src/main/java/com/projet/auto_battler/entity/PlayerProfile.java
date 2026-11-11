package main.java.com.projet.auto_battler.entity;

import java.lang.annotation.Inherited;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class PlayerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    int totalVictories;
    @Column
    int totalDefeats;
    @Column
    int gold;
    @Column
    Dungeon highestDungeonReached;
}
