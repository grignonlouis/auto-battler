package com.projet.auto_battler.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.PlayerService;
import com.projet.auto_battler.service.dto.LoginDTO;
import com.projet.auto_battler.service.dto.RegisterDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final PlayerService playerService;

    public AuthController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterDTO registerForm,
                           BindingResult result,
                           HttpSession session) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            Player player = playerService.register(registerForm);
            session.setAttribute("player", player);
            return "redirect:/";
        } catch (BusinessRuleException e) {
            String field = e.getField() != null ? e.getField() : "email";
            result.rejectValue(field, "business", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginDTO loginForm,
                        BindingResult result,
                        HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }
        Optional<Player> found = playerService.findPlayer(loginForm.getEmail(), loginForm.getPassword());
        if (found.isEmpty()) {
            // Same text for an unknown email and a bad password, so registered addresses stay hidden.
            result.reject("credentials", "Invalid email or password.");
            return "login";
        }
        session.setAttribute("player", found.get());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
