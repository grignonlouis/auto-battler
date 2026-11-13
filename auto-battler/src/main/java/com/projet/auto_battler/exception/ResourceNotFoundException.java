package com.projet.auto_battler.exception;

/**
 * Thrown when a requested resource does not exist (for example a skill with an unknown id).
 * Mapped to HTTP 404 by the exception handlers.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates the exception with a message built from the resource name and its id.
     *
     * @param resource the type of resource that was not found (for example "Skill")
     * @param id       the id that was looked up
     */
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " " + id + " not found");
    }
}