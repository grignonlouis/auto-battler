package com.projet.auto_battler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "dongeonSelect";
    }

    @GetMapping("/equipe")
    public String equipe() {
        return "teamBuilding";
    }

    @GetMapping("/donjon")
    public String donjon() {
        return "dongeonSelect";
    }

    @GetMapping("/combat")
    public String combat() {
        return "fight";
    }

    @GetMapping("/bilan")
    public String bilan() {
        return "bilan";
    }

    @GetMapping("/connexion")
    public String connexion() {
        return "login";
    }

    @GetMapping("/inscription")
    public String inscription() {
        return "register";
    }
    @GetMapping("/deconnection")
    public String deconnection() {
        return "logout";
    }
}