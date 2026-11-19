package com.projet.auto_battler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.DungeonService;
import com.projet.auto_battler.service.PlayerService;
import com.projet.auto_battler.service.dto.PlayerRequest;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;
    private final DungeonService dungeonService;

    public PlayerController(PlayerService playerService, DungeonService dungeonService) {
        this.playerService = playerService;
        this.dungeonService = dungeonService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("players", playerService.findAll(search));
        model.addAttribute("search", search);
        return "players/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("playerRequest", new PlayerRequest());
        prepareForm(model, "/players", "New player", false);
        return "players/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("playerRequest") PlayerRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/players", "New player", false);
            return "players/form";
        }
        try {
            playerService.create(request);
        } catch (BusinessRuleException e) {
            rejectBusinessError(result, e);
            prepareForm(model, "/players", "New player", false);
            return "players/form";
        }
        redirect.addFlashAttribute("success", "Player created.");
        return "redirect:/players";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("playerRequest", playerService.getForEdit(id));
        prepareForm(model, "/players/" + id, "Edit player", true);
        return "players/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("playerRequest") PlayerRequest request,
                         BindingResult result,
                         Model model,
                         HttpSession session,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/players/" + id, "Edit player", true);
            return "players/form";
        }
        try {
            playerService.update(id, request);
        } catch (BusinessRuleException e) {
            rejectBusinessError(result, e);
            prepareForm(model, "/players/" + id, "Edit player", true);
            return "players/form";
        }
        replaceSessionPlayer(session, id);
        redirect.addFlashAttribute("success", "Player updated.");
        return "redirect:/players";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        playerService.delete(id);
        dropSessionIfSamePlayer(session, id);
        redirect.addFlashAttribute("success", "Player deleted.");
        return "redirect:/players";
    }

    private void rejectBusinessError(BindingResult result, BusinessRuleException exception) {
        String field = exception.getField() != null ? exception.getField() : "username";
        result.rejectValue(field, "business", exception.getMessage());
    }

    private void prepareForm(Model model, String formAction, String pageTitle, boolean editing) {
        model.addAttribute("allDungeons", dungeonService.findAll(null));
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("editing", editing);
    }

    private void replaceSessionPlayer(HttpSession session, Long id) {
        Object current = session.getAttribute("player");
        if (current instanceof Player signedIn && id.equals(signedIn.getId())) {
            session.setAttribute("player", playerService.getEntityOrThrow(id));
        }
    }

    private void dropSessionIfSamePlayer(HttpSession session, Long id) {
        Object current = session.getAttribute("player");
        if (current instanceof Player signedIn && id.equals(signedIn.getId())) {
            session.removeAttribute("player");
        }
    }
}
