package com.projet.auto_battler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.repository.*;

@Controller
public class HomeController {

    private final DungeonRepository dungeonRepository;
    
    public HomeController(DungeonRepository dungeonRepository) {
        this.dungeonRepository = dungeonRepository;
    }

    @GetMapping("/")
    public String home() {
        return "dungeonSelect";
    }

    @GetMapping("/equipe")
    public String equipe() {
        return "teamBuilding";
    }

    @GetMapping("/donjon")
    public String donjon(Model model) {
        model.addAttribute("dungeons", dungeonRepository.findAll());
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