package com.projet.auto_battler.service.dto;

import lombok.Data;
 
@Data
public class RegisterDTO {

    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public RegisterDTO(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}