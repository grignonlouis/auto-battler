// @RestController, @RequestMapping("/api/v1/skills"). Dépend de SkillService uniquement.
// Endpoints et codes :
//  - GET    /                 -> 200, liste ; paramètre optionnel ?search=
//  - GET    /{id}             -> 200, ou 404
//  - POST   /                 -> 201 + en-tête Location (/api/v1/skills/{id}), corps @Valid SkillRequest, ou 400/409
//  - PUT    /{id}             -> 200, corps @Valid SkillRequest, ou 400/404/409
//  - DELETE /{id}             -> 204, ou 404
// Aucune logique ici : appeler le service et choisir le code HTTP. Renvoie SkillResponse, jamais l'entité.

package com.projet.auto_battler.rest;

public class SkillRestController {
    
}
