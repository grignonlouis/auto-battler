package com.projet.auto_battler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DungeonController {

    @GetMapping("/dungeons")
    public String showDungeonSelectForm() {
        return "dungeonSelect";
    }
}
