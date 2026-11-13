package com.projet.auto_battler.exception;

/**
 * Exception lancée quand une règle métier est violée
 * (nom déjà pris, équipe pleine, donjon avec deux boss...).
 * Côté API REST elle donne un code HTTP 409 (conflit).
 * On a créé notre propre classe plutôt que d'utiliser IllegalArgumentException,
 * car celle-ci peut aussi être lancée par d'autres bibliothèques et on risquerait
 * de renvoyer des 409 par erreur.
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Construit l'exception avec un message lisible par l'utilisateur
     * (c'est ce message qui sera affiché dans le formulaire ou renvoyé par l'API).
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}