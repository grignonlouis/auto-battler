package com.projet.auto_battler.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    // Empty on update keeps the current hash. Required when creating, checked in the service.
    @Size(max = 100, message = "Password must not exceed 100 characters")
    private String password;

    @NotNull(message = "Gold is required")
    @Min(value = 0, message = "Gold must not be negative")
    private Integer gold = 0;

    @NotNull(message = "Victories are required")
    @Min(value = 0, message = "Victories must not be negative")
    private Integer totalVictories = 0;

    @NotNull(message = "Defeats are required")
    @Min(value = 0, message = "Defeats must not be negative")
    private Integer totalDefeats = 0;

    private Long highestDungeonId;

    @NotNull(message = "Administrator status is required")
    private Boolean admin = false;
}
