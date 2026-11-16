package com.projet.auto_battler.entity;

/**
 * The kinds of skills a hero can use in combat.
 */
public enum SkillType {
    /** Deals damage to an enemy. */
    ATTACK,
    /** Restores health points to an ally. */
    HEAL,
    /** Absorbs incoming damage for a few turns. */
    SHIELD,
    /** Deals damage at the start of each turn. */
    POISON
}