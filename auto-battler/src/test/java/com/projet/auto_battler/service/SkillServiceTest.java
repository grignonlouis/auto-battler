// JUnit 5 + Mockito (repository mocké, pas de Spring). Cas à couvrir :
//  - create : cas nominal (le repository reçoit une entité sauvegardée)
//  - create : nom déjà existant -> BusinessRuleException
//  - update : nom pris par une AUTRE compétence -> BusinessRuleException ; même nom que soi-même -> OK
//  - findById / update / delete sur id inconnu -> ResourceNotFoundException
//  - findAll avec et sans search

package com.projet.auto_battler.service;

public class SkillServiceTest {
    
}
