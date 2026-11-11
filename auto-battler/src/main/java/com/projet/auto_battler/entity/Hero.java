package main.java.com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class Hero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String name;
    @Column
    String description;
    @Column
    Role role;
    @Column
    int health;
    @Column
    int attackDamage;
    @Column
    int defense;
    @Column
    int speed;
    @Column
    int level;
    @Column
    int experience;
    @Column
    int unlockable;

}
