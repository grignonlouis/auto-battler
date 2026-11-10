// @Service. @Transactional sur la classe, @Transactional(readOnly = true) sur les lectures.
// Dépend uniquement de SkillRepository. Aucune dépendance MVC ni JSON.
// CONTRAT (signatures à respecter, Front et Qualité codent contre elles) :
//   List<SkillResponse> findAll(String search)   // search null ou vide = tout
//   SkillResponse findById(Long id)              // ResourceNotFoundException si absent
//   SkillRequest getForEdit(Long id)             // entité -> SkillRequest, pour pré-remplir le formulaire
//   SkillResponse create(SkillRequest request)
//   SkillResponse update(Long id, SkillRequest request)
//   void delete(Long id)
//   Skill getEntityOrThrow(Long id)              // pour les AUTRES services (HeroService...), jamais pour les contrôleurs
// RÈGLES :
//  - create/update : name nettoyé (trim) ; si le nom existe déjà (insensible à la casse, hors soi-même
//    pour update) -> BusinessRuleException("Une compétence nommée ... existe déjà")
//  - update/delete d'un id inconnu -> ResourceNotFoundException
//  - delete : pour l'instant, suppression directe.
//    TODO quand Hero existera : détacher la compétence de tous les héros AVANT de supprimer
//    (Hero est le côté propriétaire de la relation, sans ça la suppression échoue sur la table de jointure).
// Le mapping entité <-> DTO se fait ICI, dans la transaction.

package com.projet.auto_battler.service;

public class SkillService {
    
}
