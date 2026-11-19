package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Equipment;
import com.projet.auto_battler.entity.Rarity;
import com.projet.auto_battler.entity.Slot;

public record EquipmentResponse(
        Long id,
        String name,
        Slot slot,
        Rarity rarity,
        int bonusAttack,
        int bonusHealth,
        int bonusDefense) {

    public static EquipmentResponse from(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getSlot(),
                equipment.getRarity(),
                equipment.getBonusAttack(),
                equipment.getBonusHealth(),
                equipment.getBonusDefense());
    }
}