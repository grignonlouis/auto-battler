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
    @Column
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @OneToOne
    private PlayerProfile profile;
    @Column 
    private boolean isAdmin;


    public Player(String name, String email, String password) {
        this.username = name;
        this.email = email;
        this.password = password;
        this.isAdmin = false;
    }
}
