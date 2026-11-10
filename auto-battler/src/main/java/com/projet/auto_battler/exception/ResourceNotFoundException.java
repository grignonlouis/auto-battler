// RuntimeException. Constructeur avec le nom de la ressource et l'id
// (message du type "Skill 12 introuvable"). Lancée par les services -> 404.

package com.projet.auto_battler.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
