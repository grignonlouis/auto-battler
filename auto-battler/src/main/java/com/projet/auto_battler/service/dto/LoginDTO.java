package com.projet.auto_battler.service.dto;

import lombok.Data;
 
/**
 * Form data accepted by the login page.
 */
@Data
public class LoginDTO {

    private String email;
    private String password;
    
    /**
     * Initializes the email and password fields.
     *
     * @param name accepted by this constructor but not stored in the login DTO
     * @param email email address submitted for login
     * @param password password submitted for login
     * @param confirmPassword accepted by this constructor but not stored in the login DTO
     */
    public LoginDTO(String name, String email, String password, String confirmPassword) {
        this.email = email;
        this.password = password;
    }
}