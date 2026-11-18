package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * A registered player account.
 *
 * <p>The account has a one-to-one JPA association with a profile. Passwords are stored as hashes.
 */
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


    /**
     * Creates a non-administrator account with the supplied password hash.
     *
     * @param name the player's username
     * @param email the player's email address
     * @param password the encoded password value to store
     */
    public Player(String name, String email, String password) {
        this.username = name;
        this.email = email;
        this.passwordHash = password;
        this.isAdmin = false;
    }
}
