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

/**
 * Web controller serving the HTML pages for dungeons.
 * It receives browser requests under /dungeons, asks the services for data and picks the view to render.
 * It contains no business rules: those live in DungeonService.
 */
@Controller
@RequestMapping("/dungeons")
public class DungeonController {

    private final DungeonService dungeonService;
    private final MonsterService monsterService;

    /**
     * Creates the controller with its services.
     *
     * @param dungeonService the service handling dungeon logic
     * @param monsterService the service providing the monsters offered in the form
     */
    public DungeonController(DungeonService dungeonService, MonsterService monsterService) {
        this.dungeonService = dungeonService;
        this.monsterService = monsterService;
    }

    /**
     * Shows the list of dungeons, optionally filtered by a search text.
     *
     * @param search optional text that must be contained in the dungeon name
     * @param model  the model passed to the view
     * @return the name of the list view
     */
    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("dungeons", dungeonService.findAll(search));
        model.addAttribute("search", search);
        return "dungeons/list";
    }

    /**
     * Shows the empty form used to create a dungeon.
     *
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("dungeonRequest", new DungeonRequest());
        prepareForm(model, "/dungeons", "New dungeon");
        return "dungeons/form";
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

    /**
     * Shows the edit form pre-filled with the current values of a dungeon.
     *
     * @param id    the id of the dungeon to edit
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("dungeonRequest", dungeonService.getForEdit(id));
        prepareForm(model, "/dungeons/" + id, "Edit dungeon");
        return "dungeons/form";
    }

    /**
     * Handles the edit form, with the same error handling as the creation form.
     *
     * @param id       the id of the dungeon to update
     * @param request  the submitted form data
     * @param result   the validation result
     * @param model    the model passed to the view
     * @param redirect holder for the flash message
     * @return the form view on error, otherwise a redirect to the list
     */
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

    /**
     * Deletes a dungeon and redirects to the list with a confirmation message.
     * It is a POST because HTML forms can only submit GET or POST requests.
     *
     * @param id       the id of the dungeon to delete
     * @param redirect holder for the flash message
     * @return a redirect to the list
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        dungeonService.delete(id);
        redirect.addFlashAttribute("success", "Dungeon deleted.");
        return "redirect:/dungeons";
    }

    /**
     * Attaches a business rule error to the form field it relates to, or to the name field by default.
     *
     * @param result    the binding result of the form
     * @param exception the business rule violation raised by the service
     */
    private void rejectBusinessError(BindingResult result, BusinessRuleException exception) {
        String field = exception.getField() != null ? exception.getField() : "name";
        result.rejectValue(field, "business", exception.getMessage());
    }

    /**
     * Adds to the model the values every display of the form needs, including the monsters to choose from.
     *
     * @param model      the model passed to the view
     * @param formAction the URL the form is submitted to
     * @param pageTitle  the title shown on the page
     */
    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("allMonsters", monsterService.findAll(null));
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}