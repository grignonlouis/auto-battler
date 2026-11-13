package com.projet.auto_battler.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet.auto_battler.service.dto.LoginDTO;
import com.projet.auto_battler.service.dto.RegisterDTO;
import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.service.PlayerService;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;



@Controller 
public class AuthController {

    private final PlayerService playerService;

    AuthController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/register")
    String register(){
        return "register";
    }

    @PostMapping("/register")
    String register(@ModelAttribute RegisterDTO userDTO,
        RedirectAttributes redirection,
        HttpSession session){
        
        System.out.println("DTO=" + userDTO);
        
        try {
            Player player = playerService.createUser(userDTO);
            redirection.addFlashAttribute("welcome", "Welcome " + player.getUsername());
            session.setAttribute("player", player);
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirection.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    String login(){
        return "login";
    }

    @PostMapping("/login")
    String login(@ModelAttribute LoginDTO dto,
        RedirectAttributes redirection,
        HttpSession session){
        System.out.println("dto=" + dto);

        Optional<Player> dbPlayer = playerService.findPlayer(dto.getEmail(),dto.getPassword());
        if(!dbPlayer.isPresent()){
            redirection.addFlashAttribute("error", "Utilisateur non trouvé");
            return "redirect:/login";
        }
        
        redirection.addFlashAttribute("welcome", "Welcome " + dbPlayer.get().getUsername());
        // player.getProjects().add(new Project("Cats","cats are cute","chatons3.jpeg"));
        // player.getProjects().add(new Project("Duck","duck is independant","canard.jpeg"));
        session.setAttribute("player", dbPlayer.get());
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}
