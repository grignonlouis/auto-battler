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

import com.projet.auto_battler.service.PlayerService;
import com.projet.auto_battler.service.dto.PlayerRequest;
import com.projet.auto_battler.service.dto.PlayerResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerRestController {

    private final PlayerService playerService;

    public PlayerRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerResponse> findAll(@RequestParam(required = false) String search) {
        return playerService.findAll(search);
    }

    @GetMapping("/{id}")
    public PlayerResponse findById(@PathVariable Long id) {
        return playerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse created = playerService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public PlayerResponse update(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
        return playerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
