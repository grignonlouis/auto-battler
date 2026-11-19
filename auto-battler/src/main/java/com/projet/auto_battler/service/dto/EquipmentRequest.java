package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Rarity;
import com.projet.auto_battler.entity.Slot;

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
public class EquipmentRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Slot is required")
    private Slot slot;

    @NotNull(message = "Rarity is required")
    private Rarity rarity;

    @Min(value = 0, message = "Attack bonus must not be negative")
    private int bonusAttack;

    @Min(value = 0, message = "Health bonus must not be negative")
    private int bonusHealth;

    @Min(value = 0, message = "Defense bonus must not be negative")
    private int bonusDefense;
}