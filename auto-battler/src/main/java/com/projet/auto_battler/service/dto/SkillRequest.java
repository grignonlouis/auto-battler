package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.SkillType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Input data used to create or update a skill.
 * It backs both the Thymeleaf form and the JSON body of the REST API.
 * It is a regular class with getters and setters so that th:field can bind to it.
 */
@Getter
@Setter
@NoArgsConstructor
public class SkillRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Type is required")
    private SkillType skillType;

    @Min(value = 0, message = "Power must not be negative")
    private int power;

    @Min(value = 0, message = "Cooldown must not be negative")
    private int cooldown;

    @Min(value = 0, message = "Duration must not be negative")
    private int duration;
}