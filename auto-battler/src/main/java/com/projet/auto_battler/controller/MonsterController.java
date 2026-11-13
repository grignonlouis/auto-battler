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

/**
 * Web controller serving the HTML pages for monsters.
 * It receives browser requests under /monsters, asks the service for data and picks the view to render.
 * It contains no business rules: those live in MonsterService.
 */
@Controller
@RequestMapping("/monsters")
public class MonsterController {

    private final MonsterService monsterService;

    /**
     * Creates the controller with its service.
     *
     * @param monsterService the service handling monster logic
     */
    public MonsterController(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    /**
     * Shows the list of monsters, optionally filtered by a search text.
     *
     * @param search optional text that must be contained in the monster name
     * @param model  the model passed to the view
     * @return the name of the list view
     */
    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("monsters", monsterService.findAll(search));
        model.addAttribute("search", search);
        return "monsters/list";
    }

    /**
     * Shows the empty form used to create a monster.
     *
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("monsterRequest", new MonsterRequest());
        prepareForm(model, "/monsters", "New monster");
        return "monsters/form";
    }

    /**
     * Handles the creation form.
     * On validation errors or a business rule violation, the form is displayed again with the messages.
     * On success, redirects to the list with a confirmation message.
     *
     * @param request  the submitted form data
     * @param result   the validation result
     * @param model    the model passed to the view
     * @param redirect holder for the flash message
     * @return the form view on error, otherwise a redirect to the list
     */
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

    /**
     * Shows the edit form pre-filled with the current values of a monster.
     *
     * @param id    the id of the monster to edit
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("monsterRequest", monsterService.getForEdit(id));
        prepareForm(model, "/monsters/" + id, "Edit monster");
        return "monsters/form";
    }

    /**
     * Handles the edit form, with the same error handling as the creation form.
     *
     * @param id       the id of the monster to update
     * @param request  the submitted form data
     * @param result   the validation result
     * @param model    the model passed to the view
     * @param redirect holder for the flash message
     * @return the form view on error, otherwise a redirect to the list
     */
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

    /**
     * Deletes a monster and redirects to the list with a confirmation message.
     * It is a POST because HTML forms can only submit GET or POST requests.
     *
     * @param id       the id of the monster to delete
     * @param redirect holder for the flash message
     * @return a redirect to the list
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        monsterService.delete(id);
        redirect.addFlashAttribute("success", "Monster deleted.");
        return "redirect:/monsters";
    }

    /**
     * Adds to the model the values every display of the form needs.
     *
     * @param model      the model passed to the view
     * @param formAction the URL the form is submitted to
     * @param pageTitle  the title shown on the page
     */
    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("monsterTypes", MonsterType.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}