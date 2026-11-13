package com.projet.auto_battler.exception;

/**
 * Exception lancée quand on cherche quelque chose qui n'existe pas
 * (par exemple une compétence avec un id inconnu).
 * C'est une RuntimeException : on n'est pas obligé de la déclarer partout avec "throws".
 * Côté web elle donne une page 404, côté API REST un code HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construit le message à partir du nom de la ressource et de son id.
     * Exemple : new ResourceNotFoundException("Compétence", 12) donne "Compétence 12 introuvable".
     */
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " " + id + " introuvable");
    }
}