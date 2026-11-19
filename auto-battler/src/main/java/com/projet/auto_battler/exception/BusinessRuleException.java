package com.projet.auto_battler.exception;

public class BusinessRuleException extends RuntimeException {

    private final String field;

    public BusinessRuleException(String message) {
        this(null, message);
    }

    public BusinessRuleException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}