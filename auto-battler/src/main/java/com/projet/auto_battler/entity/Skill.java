package com.projet.auto_battler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Une compétence qu'un héros peut utiliser en combat (attaque, soin, bouclier, poison).
 * @Entity veut dire que cette classe correspond à une table de la base de données :
 * un objet Skill = une ligne de la table.
 * On utilise @Getter/@Setter de Lombok et surtout PAS @Data, qui cause des problèmes
 * avec les relations JPA (boucles infinies).
 * Cette classe n'a aucune relation : c'est Hero qui portera le lien Hero-Skill.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor // JPA a besoin d'un constructeur vide pour créer les objets
public class Skill {

    // Identifiant unique, généré automatiquement par la base
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de la compétence : obligatoire et unique dans toute la base
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Description (jusqu'à 500 caractères)
    @Column(length = 500)
    private String description;

    // Type de compétence. EnumType.STRING : on stocke le texte ("HEAL") et pas un numéro,
    // comme ça l'ordre de l'enum peut changer sans casser la base.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillType skillType;

    // Dégâts, soin, ou valeur du bouclier/poison selon le type
    private int power;

    // Nombre de tours à attendre avant de pouvoir réutiliser la compétence
    private int cooldown;

    // Nombre de tours que dure l'effet (poison et bouclier), 0 pour les autres
    private int duration;
}