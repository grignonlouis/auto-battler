// @Entity. Lombok : @Getter, @Setter, @NoArgsConstructor. INTERDIT : @Data.
// Champs :
//  - id : Long, @Id + @GeneratedValue(strategy = IDENTITY)
//  - name : String, obligatoire, unique en base (@Column(nullable = false, unique = true, length = 100))
//  - description : String, optionnel, longueur max 500
//  - skillType : SkillType, @Enumerated(EnumType.STRING), obligatoire
//  - power : int (dégâts, soin ou valeur du bouclier/poison)
//  - cooldown : int (tours d'attente entre deux utilisations)
//  - duration : int (tours, utile pour POISON et SHIELD, 0 sinon)
// Attention aux mots réservés H2 : ne pas nommer une colonne value, user, order, key...
// Aucune relation ici : la relation Hero <-> Skill est portée par Hero (côté propriétaire).

package com.projet.auto_battler.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    SkillType skillType;
    int power;
    int cooldown;
    int duration;
}
