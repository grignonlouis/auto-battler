package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String passwordHash;
    @OneToOne
    private PlayerProfile profile;

    private boolean isAdmin;


    public Player(String name, String email, String password) {
        this.username = name;
        this.email = email;
        this.passwordHash = password;
        this.isAdmin = false;
    }
}
