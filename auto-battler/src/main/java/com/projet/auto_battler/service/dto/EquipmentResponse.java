package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Equipment;
import com.projet.auto_battler.entity.Rarity;
import com.projet.auto_battler.entity.Slot;

/**
 * Output data describing a piece of equipment, used by the web pages and the REST API.
 *
 * @param id           the equipment id
 * @param name         the equipment name
 * @param slot         the slot it fits in
 * @param rarity       how rare it is
 * @param bonusAttack  the attack bonus
 * @param bonusHealth  the health bonus
 * @param bonusDefense the defense bonus
 */
public record EquipmentResponse(
        Long id,
        String name,
        Slot slot,
        Rarity rarity,
        int bonusAttack,
        int bonusHealth,
        int bonusDefense) {

    /**
     * Converts an Equipment entity into an EquipmentResponse by copying its fields.
     *
     * @param equipment the entity to convert
     * @return the matching response object
     */
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