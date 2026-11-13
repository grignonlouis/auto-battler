package com.projet.auto_battler.exception;

/**
 * Thrown when a business rule is violated (duplicate name, full team, second boss in a dungeon...).
 * Mapped to HTTP 409 by the REST exception handler.
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Creates the exception with a user-readable message.
     *
     * @param message the explanation shown in forms or returned by the API
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}