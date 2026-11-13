// @Controller, @RequestMapping("/skills"). Dépend de SkillService uniquement. Retourne des noms de vues.
// Routes :
//  - GET  /skills            : liste. Paramètre optionnel search. Modèle : "skills" (liste de SkillResponse), "search".
//  - GET  /skills/new        : formulaire vide. Modèle : "skillRequest" (new SkillRequest()), "skillTypes" (SkillType.values()),
//                              "formAction" = /skills, "pageTitle"
//  - POST /skills            : création. @Valid + BindingResult. Si erreurs de validation : réafficher le formulaire (pas de redirect).
//                              Si BusinessRuleException : l'attacher au champ name via BindingResult puis réafficher le formulaire.
//                              Sinon : redirect vers /skills avec message flash de succès (RedirectAttributes).
//  - GET  /skills/{id}/edit  : formulaire pré-rempli via service.getForEdit(id). formAction = /skills/{id}.
//  - POST /skills/{id}       : modification, même gestion des erreurs que la création.
//  - POST /skills/{id}/delete: suppression puis redirect vers /skills avec message flash. (Les navigateurs ne font que GET/POST
//                              depuis un formulaire : c'est pour ça que la suppression passe par POST.)
// Ne contient aucune règle métier.

package com.projet.auto_battler.controller;

public class SkillController {
    
}
