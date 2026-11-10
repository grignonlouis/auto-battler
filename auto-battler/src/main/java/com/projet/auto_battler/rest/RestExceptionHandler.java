// @RestControllerAdvice limité au package rest (attribut basePackages) : il ne doit PAS
// intercepter les contrôleurs Thymeleaf.
// Gère :
//  - ResourceNotFoundException            -> 404
//  - BusinessRuleException                -> 409
//  - MethodArgumentNotValidException      -> 400 avec fieldErrors (champ + message pour chaque erreur @Valid)
//  - HttpMessageNotReadableException      -> 400 (JSON invalide, valeur d'enum inconnue)
//  - MethodArgumentTypeMismatchException  -> 400 (ex. /skills/abc)
// Tout renvoie un ApiError. Ce fichier sera réutilisé par toutes les ressources : ne rien mettre de spécifique à Skill.

package com.projet.auto_battler.rest;

public class RestExceptionHandler {
    
}
