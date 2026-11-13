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

/**
 * Web controller serving the HTML pages for skills.
 * It receives browser requests under /skills, asks the service for data and picks the view to render.
 * It contains no business rules: those live in SkillService.
 */
@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    /**
     * Creates the controller with its service.
     *
     * @param skillService the service handling skill logic
     */
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Shows the list of skills, optionally filtered by a search text.
     *
     * @param search optional text that must be contained in the skill name
     * @param model  the model passed to the view
     * @return the name of the list view
     */
    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("skills", skillService.findAll(search));
        model.addAttribute("search", search);
        return "skills/list";
    }

    /**
     * Shows the empty form used to create a skill.
     *
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("skillRequest", new SkillRequest());
        prepareForm(model, "/skills", "New skill");
        return "skills/form";
    }

    /**
     * Handles the creation form.
     * On validation errors or a business rule violation, the form is displayed again with the messages.
     * On success, redirects to the list with a confirmation message.
     * The BindingResult parameter must directly follow the validated parameter.
     *
     * @param request  the submitted form data
     * @param result   the validation result
     * @param model    the model passed to the view
     * @param redirect holder for the flash message
     * @return the form view on error, otherwise a redirect to the list
     */
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

    /**
     * Shows the edit form pre-filled with the current values of a skill.
     *
     * @param id    the id of the skill to edit
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("skillRequest", skillService.getForEdit(id));
        prepareForm(model, "/skills/" + id, "Edit skill");
        return "skills/form";
    }

    /**
     * Handles the edit form, with the same error handling as the creation form.
     *
     * @param id       the id of the skill to update
     * @param request  the submitted form data
     * @param result   the validation result
     * @param model    the model passed to the view
     * @param redirect holder for the flash message
     * @return the form view on error, otherwise a redirect to the list
     */
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

    /**
     * Deletes a skill and redirects to the list with a confirmation message.
     * It is a POST because HTML forms can only submit GET or POST requests.
     *
     * @param id       the id of the skill to delete
     * @param redirect holder for the flash message
     * @return a redirect to the list
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        skillService.delete(id);
        redirect.addFlashAttribute("success", "Skill deleted.");
        return "redirect:/skills";
    }

    /**
     * Adds to the model the values every display of the form needs.
     *
     * @param model      the model passed to the view
     * @param formAction the URL the form is submitted to
     * @param pageTitle  the title shown on the page
     */
    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("skillTypes", SkillType.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}