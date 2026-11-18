package com.projet.auto_battler.exception;

/**
 * Thrown when a business rule is violated (for example, a duplicate name or a second boss in a dungeon).
 * It can optionally name the form field the error relates to.
 * No REST exception handler currently maps this exception to an HTTP response.
 */
public class BusinessRuleException extends RuntimeException {

    private final String field;

    /**
     * Creates the exception with a user-readable message and no specific field.
     *
     * @param message the explanation shown in forms or returned by the API
     */
    public BusinessRuleException(String message) {
        this(null, message);
    }

    /**
     * Creates the exception with a user-readable message tied to a form field.
     *
     * @param field   the name of the form field the error relates to
     * @param message the explanation shown in forms or returned by the API
     */
    public BusinessRuleException(String field, String message) {
        super(message);
        this.field = field;
    }

    /**
     * Returns the form field this error relates to.
     *
     * @return the field name, or null if the error is not tied to a field
     */
    public String getField() {
        return field;
    }
}