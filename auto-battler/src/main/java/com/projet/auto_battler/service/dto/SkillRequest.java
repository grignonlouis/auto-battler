// CLASSE (pas un record : th:field a besoin de getters/setters). Lombok : @Getter @Setter @NoArgsConstructor.
// Sert à la fois pour le formulaire Thymeleaf et pour le corps JSON des POST/PUT.
// Champs et validations (jakarta.validation.constraints) :
//  - name : @NotBlank, @Size(max = 100)
//  - description : @Size(max = 500)
//  - skillType : @NotNull
//  - power : @Min(0)
//  - cooldown : @Min(0)
//  - duration : @Min(0)
// Valeurs par défaut raisonnables pour les int (0) pour que le formulaire "nouveau" s'affiche proprement.

package com.projet.auto_battler.service.dto;

public class SkillRequest {
    
}
