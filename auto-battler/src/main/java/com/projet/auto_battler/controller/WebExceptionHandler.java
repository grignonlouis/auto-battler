package com.projet.auto_battler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.projet.auto_battler.exception.ResourceNotFoundException;

/**
 * Handles errors raised by the controllers that render HTML pages.
 * It only applies to the controller package, so the REST API keeps its own JSON error handler.
 */
@ControllerAdvice(basePackages = "com.projet.auto_battler.controller")
public class WebExceptionHandler {

    /**
     * Renders the 404 page when a resource is not found.
     *
     * @param exception the exception thrown by a controller or service
     * @param model     the model passed to the view
     * @return the name of the 404 view
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error/404";
    }
}