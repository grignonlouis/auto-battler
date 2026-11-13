package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.MonsterType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Input data used to create or update a monster.
 * It backs both the Thymeleaf form and the JSON body of the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
public class MonsterRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Type is required")
    private MonsterType monsterType;

    @Min(value = 1, message = "Health must be at least 1")
    private int health;

    @Min(value = 0, message = "Damage must not be negative")
    private int damage;

    @Min(value = 0, message = "Defense must not be negative")
    private int defense;

    private boolean boss;
}