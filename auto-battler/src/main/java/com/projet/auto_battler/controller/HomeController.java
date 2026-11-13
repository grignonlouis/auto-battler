package com.projet.auto_battler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;

import com.projet.auto_battler.entity.Dungeon;


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
    public String donjon(Model model) {
         List<Dungeon> dungeons = List.of(
                    new Dungeon("Premier élément", "desc1", 1, true),
                    new Dungeon("Deuxième élément", "desc2", 3, true),
                    new Dungeon("Troisième élément", "desc3", 2, false)
                );
                model.addAttribute("dungeons", dungeons);
        return "dungeonSelect";
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