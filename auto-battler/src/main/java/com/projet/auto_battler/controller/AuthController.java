package com.projet.auto_battler.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.PlayerService;
import com.projet.auto_battler.service.dto.LoginDTO;
import com.projet.auto_battler.service.dto.RegisterDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final PlayerService playerService;

    public AuthController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO userDTO,
                           RedirectAttributes redirection,
                           HttpSession session) {
        try {
            Player player = playerService.createUser(userDTO);
            redirection.addFlashAttribute("welcome", "Welcome " + player.getUsername());
            session.setAttribute("player", player);
            return "redirect:/";
        } catch (BusinessRuleException e) {
            redirection.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO dto,
                        RedirectAttributes redirection,
                        HttpSession session) {
        Optional<Player> found = playerService.findPlayer(dto.getEmail(), dto.getPassword());
        if (found.isEmpty()) {
            // Same text for an unknown email and a bad password, so registered addresses stay hidden.
            redirection.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }
        Player player = found.get();
        redirection.addFlashAttribute("welcome", "Welcome " + player.getUsername());
        session.setAttribute("player", player);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}