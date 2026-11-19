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

@RestController
@RequestMapping("/api/v1/dungeons")
public class DungeonRestController {

    private final DungeonService dungeonService;

    public DungeonRestController(DungeonService dungeonService) {
        this.dungeonService = dungeonService;
    }

    @GetMapping
    public List<DungeonResponse> findAll(@RequestParam(required = false) String search) {
        return dungeonService.findAll(search);
    }

    @GetMapping("/{id}")
    public DungeonResponse findById(@PathVariable Long id) {
        return dungeonService.findById(id);
    }

    @PostMapping
    public ResponseEntity<DungeonResponse> create(@Valid @RequestBody DungeonRequest request) {
        DungeonResponse created = dungeonService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public DungeonResponse update(@PathVariable Long id, @Valid @RequestBody DungeonRequest request) {
        return dungeonService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dungeonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/monsters/{monsterId}")
    public DungeonResponse addMonster(@PathVariable Long id, @PathVariable Long monsterId) {
        return dungeonService.addMonster(id, monsterId);
    }

    @DeleteMapping("/{id}/monsters/{monsterId}")
    public DungeonResponse removeMonster(@PathVariable Long id, @PathVariable Long monsterId) {
        return dungeonService.removeMonster(id, monsterId);
    }
}