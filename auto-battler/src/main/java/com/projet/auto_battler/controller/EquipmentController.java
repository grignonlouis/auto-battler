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

@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("equipmentList", equipmentService.findAll(search));
        model.addAttribute("search", search);
        return "equipment/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("equipmentRequest", new EquipmentRequest());
        prepareForm(model, "/equipment", "New equipment");
        return "equipment/form";
    }

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

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("equipmentRequest", equipmentService.getForEdit(id));
        prepareForm(model, "/equipment/" + id, "Edit equipment");
        return "equipment/form";
    }

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

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        equipmentService.delete(id);
        redirect.addFlashAttribute("success", "Equipment deleted.");
        return "redirect:/equipment";
    }

    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("slots", Slot.values());
        model.addAttribute("rarities", Rarity.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}