package com.projet.auto_battler.service.dto;

import lombok.Data;
 
/**
 * Form data accepted by the account registration page.
 */
@Data
public class RegisterDTO {

    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    /**
     * Initializes the registration fields.
     *
     * @param username player's display name
     * @param email player's email address
     * @param password submitted password
     * @param confirmPassword repeated password used for confirmation
     */
    public RegisterDTO(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}