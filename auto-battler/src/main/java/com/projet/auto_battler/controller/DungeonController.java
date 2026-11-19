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

import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.DungeonService;
import com.projet.auto_battler.service.MonsterService;
import com.projet.auto_battler.service.dto.DungeonRequest;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/dungeons")
public class DungeonController {

    private final DungeonService dungeonService;
    private final MonsterService monsterService;

    public DungeonController(DungeonService dungeonService, MonsterService monsterService) {
        this.dungeonService = dungeonService;
        this.monsterService = monsterService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("dungeons", dungeonService.findAll(search));
        model.addAttribute("search", search);
        return "dungeons/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("dungeonRequest", new DungeonRequest());
        prepareForm(model, "/dungeons", "New dungeon");
        return "dungeons/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("dungeonRequest") DungeonRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/dungeons", "New dungeon");
            return "dungeons/form";
        }
        try {
            dungeonService.create(request);
        } catch (BusinessRuleException e) {
            rejectBusinessError(result, e);
            prepareForm(model, "/dungeons", "New dungeon");
            return "dungeons/form";
        }
        redirect.addFlashAttribute("success", "Dungeon created.");
        return "redirect:/dungeons";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("dungeonRequest", dungeonService.getForEdit(id));
        prepareForm(model, "/dungeons/" + id, "Edit dungeon");
        return "dungeons/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("dungeonRequest") DungeonRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/dungeons/" + id, "Edit dungeon");
            return "dungeons/form";
        }
        try {
            dungeonService.update(id, request);
        } catch (BusinessRuleException e) {
            rejectBusinessError(result, e);
            prepareForm(model, "/dungeons/" + id, "Edit dungeon");
            return "dungeons/form";
        }
        redirect.addFlashAttribute("success", "Dungeon updated.");
        return "redirect:/dungeons";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        dungeonService.delete(id);
        redirect.addFlashAttribute("success", "Dungeon deleted.");
        return "redirect:/dungeons";
    }

    private void rejectBusinessError(BindingResult result, BusinessRuleException exception) {
        String field = exception.getField() != null ? exception.getField() : "name";
        result.rejectValue(field, "business", exception.getMessage());
    }

    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("allMonsters", monsterService.findAll(null));
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}