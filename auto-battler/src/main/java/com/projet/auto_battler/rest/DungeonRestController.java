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

import com.projet.auto_battler.service.DungeonService;
import com.projet.auto_battler.service.dto.DungeonRequest;
import com.projet.auto_battler.service.dto.DungeonResponse;

import jakarta.validation.Valid;

/**
 * REST API for dungeons under /api/v1/dungeons.
 * It only translates HTTP into service calls and chooses the status codes.
 * Errors (400, 404, 409) are turned into JSON by the REST exception handler.
 */
@RestController
@RequestMapping("/api/v1/dungeons")
public class DungeonRestController {

    private final DungeonService dungeonService;

    /**
     * Creates the controller with its service.
     *
     * @param dungeonService the service handling dungeon logic
     */
    public DungeonRestController(DungeonService dungeonService) {
        this.dungeonService = dungeonService;
    }

    /**
     * Lists the dungeons, optionally filtered by name.
     *
     * @param search optional text that must be contained in the dungeon name
     * @return 200 with the list of dungeons
     */
    @GetMapping
    public List<DungeonResponse> findAll(@RequestParam(required = false) String search) {
        return dungeonService.findAll(search);
    }

    /**
     * Returns one dungeon.
     *
     * @param id the dungeon id
     * @return 200 with the dungeon, or 404 if it does not exist
     */
    @GetMapping("/{id}")
    public DungeonResponse findById(@PathVariable Long id) {
        return dungeonService.findById(id);
    }

    /**
     * Creates a dungeon.
     *
     * @param request the validated JSON body
     * @return 201 with the created dungeon and its Location header, 400 on validation errors,
     *         404 if a monster does not exist, 409 if the name is taken or the monsters include several bosses
     */
    @PostMapping
    public ResponseEntity<DungeonResponse> create(@Valid @RequestBody DungeonRequest request) {
        DungeonResponse created = dungeonService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates a dungeon.
     *
     * @param id      the id of the dungeon to update
     * @param request the validated JSON body
     * @return 200 with the updated dungeon, 400 on validation errors, 404 if unknown,
     *         409 if the name is taken or the monsters include several bosses
     */
    @PutMapping("/{id}")
    public DungeonResponse update(@PathVariable Long id, @Valid @RequestBody DungeonRequest request) {
        return dungeonService.update(id, request);
    }

    /**
     * Deletes a dungeon.
     *
     * @param id the id of the dungeon to delete
     * @return 204 with no body, or 404 if it does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dungeonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a monster to a dungeon.
     *
     * @param id        the dungeon id
     * @param monsterId the id of the monster to add
     * @return 200 with the updated dungeon, 404 if the dungeon or the monster does not exist,
     *         409 if the monster is a second boss
     */
    @PutMapping("/{id}/monsters/{monsterId}")
    public DungeonResponse addMonster(@PathVariable Long id, @PathVariable Long monsterId) {
        return dungeonService.addMonster(id, monsterId);
    }

    /**
     * Removes a monster from a dungeon.
     *
     * @param id        the dungeon id
     * @param monsterId the id of the monster to remove
     * @return 200 with the updated dungeon, or 404 if the dungeon or the monster does not exist
     */
    @DeleteMapping("/{id}/monsters/{monsterId}")
    public DungeonResponse removeMonster(@PathVariable Long id, @PathVariable Long monsterId) {
        return dungeonService.removeMonster(id, monsterId);
    }
}