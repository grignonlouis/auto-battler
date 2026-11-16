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

/**
 * Web controller handling registration, login and logout.
 * The logged-in player is kept in the HTTP session under the "player" attribute.
 * It contains no business rules: those live in PlayerService.
 */
@Controller
public class AuthController {

    private final PlayerService playerService;

    /**
     * Creates the controller with its service.
     *
     * @param playerService the service handling player logic
     */
    public AuthController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Shows the registration form.
     *
     * @return the name of the registration view
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    /**
     * Handles the registration form.
     * On success, the new player is logged in and redirected to the dashboard.
     * If a business rule fails (for example the email is already used), the user is sent back
     * to the form with the error message.
     *
     * @param userDTO     the submitted form data
     * @param redirection holder for the flash messages
     * @param session     the HTTP session in which the player is stored
     * @return a redirect to the dashboard on success, otherwise to the registration form
     */
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

    /**
     * Shows the login form.
     *
     * @return the name of the login view
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * Handles the login form.
     * The error message is the same whether the email or the password is wrong,
     * so that it does not reveal which emails are registered.
     *
     * @param dto         the submitted email and password
     * @param redirection holder for the flash messages
     * @param session     the HTTP session in which the player is stored
     * @return a redirect to the dashboard on success, otherwise to the login form
     */
    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO dto,
                        RedirectAttributes redirection,
                        HttpSession session) {
        Optional<Player> found = playerService.findPlayer(dto.getEmail(), dto.getPassword());
        if (found.isEmpty()) {
            redirection.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }
        Player player = found.get();
        redirection.addFlashAttribute("welcome", "Welcome " + player.getUsername());
        session.setAttribute("player", player);
        return "redirect:/";
    }

    /**
     * Logs the player out by ending the HTTP session.
     *
     * @param session the HTTP session to invalidate
     * @return a redirect to the home page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}