package com.projet.auto_battler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web controller serving the home page of the application.
 * Other screens are served by the controller of their own resource.
 */
@Controller
public class HomeController {

    /**
     * Creates the controller.
     */
    public HomeController() {
    }

    /**
     * Shows the home page.
     *
     * @return the name of the home view
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}