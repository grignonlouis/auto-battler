package com.projet.auto_battler.entity;

/**
 * Les types de compétences possibles.
 */
public enum SkillType {
    ATTACK, // inflige des dégâts
    HEAL,   // rend des points de vie
    SHIELD, // protège pendant quelques tours
    POISON  // inflige des dégâts à chaque tour
}