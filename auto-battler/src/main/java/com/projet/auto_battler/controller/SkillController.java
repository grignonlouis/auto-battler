// @Controller, @RequestMapping("/skills"). Dépend de SkillService uniquement. Retourne des noms de vues.
// Routes :
//  - GET  /skills            : liste. Paramètre optionnel search. Modèle : "skills" (liste de SkillResponse), "search".
//  - GET  /skills/new        : formulaire vide. Modèle : "skillRequest" (new SkillRequest()), "skillTypes" (SkillType.values()),
//                              "formAction" = /skills, "pageTitle"
//  - POST /skills            : création. @Valid + BindingResult. Si erreurs de validation : réafficher le formulaire (pas de redirect).
//                              Si BusinessRuleException : l'attacher au champ name via BindingResult puis réafficher le formulaire.
//                              Sinon : redirect vers /skills avec message flash de succès (RedirectAttributes).
//  - GET  /skills/{id}/edit  : formulaire pré-rempli via service.getForEdit(id). formAction = /skills/{id}.
//  - POST /skills/{id}       : modification, même gestion des erreurs que la création.
//  - POST /skills/{id}/delete: suppression puis redirect vers /skills avec message flash. (Les navigateurs ne font que GET/POST
//                              depuis un formulaire : c'est pour ça que la suppression passe par POST.)
// Ne contient aucune règle métier.

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
 * Contrôleur web (pages HTML) pour les compétences.
 * Il reçoit les demandes du navigateur sur les adresses qui commencent par /skills,
 * demande les données au service, puis choisit la page HTML à afficher.
 * Il ne contient aucune règle métier : tout ça se passe dans SkillService.
 */
@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    /**
     * Constructeur : Spring nous donne automatiquement le SkillService
     * (c'est l'injection de dépendances), on n'a pas besoin de faire "new".
     */
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Affiche la liste des compétences (page /skills).
     * Si l'utilisateur a tapé quelque chose dans la barre de recherche,
     * on ne garde que les compétences dont le nom correspond.
     * On renvoie le nom de la page HTML à afficher : skills/list.
     */
    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        // On demande la liste au service (il gère le cas "pas de recherche")
        model.addAttribute("skills", skillService.findAll(search));
        // On renvoie aussi le texte recherché pour le garder dans la barre de recherche
        model.addAttribute("search", search);
        return "skills/list";
    }

    /**
     * Affiche le formulaire vide pour créer une compétence (page /skills/new).
     * On donne à la page un SkillRequest vide à remplir, la liste des types
     * possibles pour la liste déroulante, et l'adresse où envoyer le formulaire.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("skillRequest", new SkillRequest());
        prepareForm(model, "/skills", "Nouvelle compétence");
        return "skills/form";
    }

    /**
     * Reçoit le formulaire de création une fois que l'utilisateur clique sur "Enregistrer".
     * Étapes :
     * 1. Spring remplit un SkillRequest avec ce qui a été tapé et vérifie les règles (@Valid).
     *    Attention : BindingResult doit être juste après le paramètre @Valid.
     * 2. S'il y a des erreurs (nom vide, nombre négatif...), on réaffiche le formulaire
     *    avec les messages d'erreur, sans rien enregistrer.
     * 3. Sinon on demande au service de créer la compétence. S'il refuse (nom déjà pris),
     *    il lance une BusinessRuleException : on l'attache au champ "name" pour que
     *    le message s'affiche sous ce champ, et on réaffiche le formulaire.
     * 4. Si tout va bien, on redirige vers la liste avec un message de confirmation.
     *    On redirige (au lieu d'afficher directement) pour qu'un F5 ne renvoie pas le formulaire.
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("skillRequest") SkillRequest request, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/skills", "Nouvelle compétence");
            return "skills/form";
        }
        try {
            skillService.create(request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/skills", "Nouvelle compétence");
            return "skills/form";
        }
        redirect.addFlashAttribute("success", "Compétence créée.");
        return "redirect:/skills";
    }

    /**
     * Affiche le formulaire de modification d'une compétence (page /skills/{id}/edit).
     * C'est le même formulaire que pour la création, mais déjà rempli avec
     * les valeurs actuelles (getForEdit) et qui sera envoyé vers /skills/{id}.
     * Si l'id n'existe pas, le service lance une ResourceNotFoundException (page 404).
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("skillRequest", skillService.getForEdit(id));
        prepareForm(model, "/skills/" + id, "Modifier la compétence");
        return "skills/form";
    }

    /**
     * Reçoit le formulaire de modification.
     * Fonctionne comme la création : on vérifie les erreurs de validation, puis on demande
     * au service de modifier (il refuse si le nom est déjà pris par une autre compétence).
     * On récupère l'id de la compétence dans l'adresse (@PathVariable).
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("skillRequest") SkillRequest request, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, "/skills/" + id, "Modifier la compétence");
            return "skills/form";
        }
        try {
            skillService.update(id, request);
        } catch (BusinessRuleException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            prepareForm(model, "/skills/" + id, "Modifier la compétence");
            return "skills/form";
        }
        redirect.addFlashAttribute("success", "Compétence modifiée.");
        return "redirect:/skills";
    }

    /**
     * Supprime une compétence (appelée quand on clique sur "Supprimer").
     * C'est un POST et pas un DELETE car un formulaire HTML ne sait faire que GET et POST.
     * Après la suppression, on redirige vers la liste avec un message de confirmation.
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        skillService.delete(id);
        redirect.addFlashAttribute("success", "Compétence supprimée.");
        return "redirect:/skills";
    }

    /**
     * Méthode utilitaire (privée) : met dans le panier (Model) les trois choses
     * dont le formulaire a besoin à chaque affichage. On la met à part pour ne pas
     * répéter les mêmes lignes dans toutes les méthodes qui affichent le formulaire.
     * - skillTypes : les types possibles (ATTACK, HEAL, SHIELD, POISON) pour la liste déroulante
     * - formAction : l'adresse où le formulaire sera envoyé
     * - pageTitle : le titre affiché en haut de la page
     */
    private void prepareForm(Model model, String formAction, String pageTitle) {
        model.addAttribute("skillTypes", SkillType.values());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
    }
}