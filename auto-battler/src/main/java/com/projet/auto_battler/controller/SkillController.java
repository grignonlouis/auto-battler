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

import com.projet.auto_battler.entity.SkillType;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.SkillService;
import com.projet.auto_battler.service.dto.SkillRequest;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("skills", skillService.findAll(search));
        model.addAttribute("search", search);
        return "skills/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("skillRequest", new SkillRequest());
        prepareForm(model, "/skills", "New skill");
        return "skills/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("skillRequest") SkillRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/skills", "New skill");
            return "skills/form";
        }
        try {
            skillService.create(request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/skills", "New skill");
            return "skills/form";
        }
        redirect.addFlashAttribute("success", "Skill created.");
        return "redirect:/skills";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("skillRequest", skillService.getForEdit(id));
        prepareForm(model, "/skills/" + id, "Edit skill");
        return "skills/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("skillRequest") SkillRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/skills/" + id, "Edit skill");
            return "skills/form";
        }
        try {
            skillService.update(id, request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/skills/" + id, "Edit skill");
            return "skills/form";
        }
        redirect.addFlashAttribute("success", "Skill updated.");
        return "redirect:/skills";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        skillService.delete(id);
        redirect.addFlashAttribute("success", "Skill deleted.");
        return "redirect:/skills";
    }

    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("skillTypes", SkillType.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}