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

@RestController
@RequestMapping("/api/v1/monsters")
public class MonsterRestController {

    private final MonsterService monsterService;

    public MonsterRestController(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    @GetMapping
    public List<MonsterResponse> findAll(@RequestParam(required = false) String search) {
        return monsterService.findAll(search);
    }

    @GetMapping("/{id}")
    public MonsterResponse findById(@PathVariable Long id) {
        return monsterService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MonsterResponse> create(@Valid @RequestBody MonsterRequest request) {
        MonsterResponse created = monsterService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public MonsterResponse update(@PathVariable Long id, @Valid @RequestBody MonsterRequest request) {
        return monsterService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        monsterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}