package com.projet.auto_battler.service.dto;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
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
public class DungeonRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Difficulty is required")
    @Min(value = 1, message = "Difficulty must be at least 1")
    @Max(value = 10, message = "Difficulty must not exceed 10")
    private Integer difficulty;

    @NotNull(message = "Unlocked status is required")
    private Boolean unlocked;

    private Set<Long> monsterIds = new HashSet<>();
}