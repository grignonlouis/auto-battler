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

import com.projet.auto_battler.entity.MonsterType;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.MonsterService;
import com.projet.auto_battler.service.dto.MonsterRequest;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/monsters")
public class MonsterController {

    private final MonsterService monsterService;

    public MonsterController(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("monsters", monsterService.findAll(search));
        model.addAttribute("search", search);
        return "monsters/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("monsterRequest", new MonsterRequest());
        prepareForm(model, "/monsters", "New monster");
        return "monsters/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("monsterRequest") MonsterRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/monsters", "New monster");
            return "monsters/form";
        }
        try {
            monsterService.create(request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/monsters", "New monster");
            return "monsters/form";
        }
        redirect.addFlashAttribute("success", "Monster created.");
        return "redirect:/monsters";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("monsterRequest", monsterService.getForEdit(id));
        prepareForm(model, "/monsters/" + id, "Edit monster");
        return "monsters/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("monsterRequest") MonsterRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/monsters/" + id, "Edit monster");
            return "monsters/form";
        }
        try {
            monsterService.update(id, request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/monsters/" + id, "Edit monster");
            return "monsters/form";
        }
        redirect.addFlashAttribute("success", "Monster updated.");
        return "redirect:/monsters";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        monsterService.delete(id);
        redirect.addFlashAttribute("success", "Monster deleted.");
        return "redirect:/monsters";
    }

    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("monsterTypes", MonsterType.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}