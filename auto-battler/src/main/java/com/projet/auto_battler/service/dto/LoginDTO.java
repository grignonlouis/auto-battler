package com.projet.auto_battler.service.dto;

import lombok.Data;
 
@Data
public class LoginDTO {

    private String email;
    private String password;
    
    public LoginDTO(String name, String email, String password, String confirmPassword) {
        this.email = email;
        this.password = password;
    }
}