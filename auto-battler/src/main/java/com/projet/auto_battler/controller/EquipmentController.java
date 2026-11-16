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

import com.projet.auto_battler.entity.Rarity;
import com.projet.auto_battler.entity.Slot;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.service.EquipmentService;
import com.projet.auto_battler.service.dto.EquipmentRequest;

import jakarta.validation.Valid;

/**
 * Web controller serving the HTML pages for equipment.
 * It receives browser requests under /equipment, asks the service for data and picks the view to render.
 * It contains no business rules: those live in EquipmentService.
 */
@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Creates the controller with its service.
     *
     * @param equipmentService the service handling equipment logic
     */
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * Shows the list of equipment, optionally filtered by a search text.
     *
     * @param search optional text that must be contained in the equipment name
     * @param model  the model passed to the view
     * @return the name of the list view
     */
    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("equipmentList", equipmentService.findAll(search));
        model.addAttribute("search", search);
        return "equipment/list";
    }

    /**
     * Shows the empty form used to create a piece of equipment.
     *
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("equipmentRequest", new EquipmentRequest());
        prepareForm(model, "/equipment", "New equipment");
        return "equipment/form";
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
    public String create(@Valid @ModelAttribute("equipmentRequest") EquipmentRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/equipment", "New equipment");
            return "equipment/form";
        }
        try {
            equipmentService.create(request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/equipment", "New equipment");
            return "equipment/form";
        }
        redirect.addFlashAttribute("success", "Equipment created.");
        return "redirect:/equipment";
    }

    /**
     * Shows the edit form pre-filled with the current values of a piece of equipment.
     *
     * @param id    the id of the equipment to edit
     * @param model the model passed to the view
     * @return the name of the form view
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("equipmentRequest", equipmentService.getForEdit(id));
        prepareForm(model, "/equipment/" + id, "Edit equipment");
        return "equipment/form";
    }

    /**
     * Handles the edit form, with the same error handling as the creation form.
     *
     * @param id       the id of the equipment to update
     * @param request  the submitted form data
     * @param result   the validation result
     * @param model    the model passed to the view
     * @param redirect holder for the flash message
     * @return the form view on error, otherwise a redirect to the list
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("equipmentRequest") EquipmentRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/equipment/" + id, "Edit equipment");
            return "equipment/form";
        }
        try {
            equipmentService.update(id, request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/equipment/" + id, "Edit equipment");
            return "equipment/form";
        }
        redirect.addFlashAttribute("success", "Equipment updated.");
        return "redirect:/equipment";
    }

    /**
     * Deletes a piece of equipment and redirects to the list with a confirmation message.
     * It is a POST because HTML forms can only submit GET or POST requests.
     *
     * @param id       the id of the equipment to delete
     * @param redirect holder for the flash message
     * @return a redirect to the list
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        equipmentService.delete(id);
        redirect.addFlashAttribute("success", "Equipment deleted.");
        return "redirect:/equipment";
    }

    /**
     * Adds to the model the values every display of the form needs.
     *
     * @param model      the model passed to the view
     * @param formAction the URL the form is submitted to
     * @param pageTitle  the title shown on the page
     */
    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("slots", Slot.values());
        model.addAttribute("rarities", Rarity.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}