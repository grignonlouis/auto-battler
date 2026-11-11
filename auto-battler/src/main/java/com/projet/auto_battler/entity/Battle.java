package main.java.com.projet.auto_battler.entity;

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
    Long id;
    @Column
    Date startedAt;
    @Column
    Date endedAt;
    @Column
    Status status;
    @Column
    int seed;
    @Column
    int turnsPlayed;
    @Column
    int xpGained;
    @Column
    int goldGained;
}
