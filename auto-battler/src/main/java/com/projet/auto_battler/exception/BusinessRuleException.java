// RuntimeException avec un message métier lisible. Lancée quand une règle métier est violée
// (nom déjà pris, équipe pleine, boss déjà présent...) -> 409.
// Remplace IllegalArgumentException du PDF : cette dernière peut être lancée par n'importe quelle
// bibliothèque, on risquerait de renvoyer des 409 par erreur.

package com.projet.auto_battler.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
