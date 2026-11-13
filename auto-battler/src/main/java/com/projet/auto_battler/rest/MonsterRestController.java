package com.projet.auto_battler.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.projet.auto_battler.service.MonsterService;
import com.projet.auto_battler.service.dto.MonsterRequest;
import com.projet.auto_battler.service.dto.MonsterResponse;

import jakarta.validation.Valid;

/**
 * REST API for monsters under /api/v1/monsters.
 * It only translates HTTP into service calls and chooses the status codes.
 * Errors (400, 404, 409) are turned into JSON by the REST exception handler.
 */
@RestController
@RequestMapping("/api/v1/monsters")
public class MonsterRestController {

    private final MonsterService monsterService;

    /**
     * Creates the controller with its service.
     *
     * @param monsterService the service handling monster logic
     */
    public MonsterRestController(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    /**
     * Lists the monsters, optionally filtered by name.
     *
     * @param search optional text that must be contained in the monster name
     * @return 200 with the list of monsters
     */
    @GetMapping
    public List<MonsterResponse> findAll(@RequestParam(required = false) String search) {
        return monsterService.findAll(search);
    }

    /**
     * Returns one monster.
     *
     * @param id the monster id
     * @return 200 with the monster, or 404 if it does not exist
     */
    @GetMapping("/{id}")
    public MonsterResponse findById(@PathVariable Long id) {
        return monsterService.findById(id);
    }

    /**
     * Creates a monster.
     *
     * @param request the validated JSON body
     * @return 201 with the created monster and its Location header, 400 on validation errors, 409 if the name is taken
     */
    @PostMapping
    public ResponseEntity<MonsterResponse> create(@Valid @RequestBody MonsterRequest request) {
        MonsterResponse created = monsterService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates a monster.
     *
     * @param id      the id of the monster to update
     * @param request the validated JSON body
     * @return 200 with the updated monster, 400 on validation errors, 404 if unknown, 409 if the name is taken
     */
    @PutMapping("/{id}")
    public MonsterResponse update(@PathVariable Long id, @Valid @RequestBody MonsterRequest request) {
        return monsterService.update(id, request);
    }

    /**
     * Deletes a monster.
     *
     * @param id the id of the monster to delete
     * @return 204 with no body, or 404 if it does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        monsterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}