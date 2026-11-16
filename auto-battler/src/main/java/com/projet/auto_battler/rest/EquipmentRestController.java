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

import com.projet.auto_battler.service.EquipmentService;
import com.projet.auto_battler.service.dto.EquipmentRequest;
import com.projet.auto_battler.service.dto.EquipmentResponse;

import jakarta.validation.Valid;

/**
 * REST API for equipment under /api/v1/equipment.
 * It only translates HTTP into service calls and chooses the status codes.
 * Errors (400, 404, 409) are turned into JSON by the REST exception handler.
 */
@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentRestController {

    private final EquipmentService equipmentService;

    /**
     * Creates the controller with its service.
     *
     * @param equipmentService the service handling equipment logic
     */
    public EquipmentRestController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * Lists the equipment, optionally filtered by name.
     *
     * @param search optional text that must be contained in the equipment name
     * @return 200 with the list of equipment
     */
    @GetMapping
    public List<EquipmentResponse> findAll(@RequestParam(required = false) String search) {
        return equipmentService.findAll(search);
    }

    /**
     * Returns one piece of equipment.
     *
     * @param id the equipment id
     * @return 200 with the equipment, or 404 if it does not exist
     */
    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable Long id) {
        return equipmentService.findById(id);
    }

    /**
     * Creates a piece of equipment.
     *
     * @param request the validated JSON body
     * @return 201 with the created equipment and its Location header, 400 on validation errors, 409 if the name is taken
     */
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        EquipmentResponse created = equipmentService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates a piece of equipment.
     *
     * @param id      the id of the equipment to update
     * @param request the validated JSON body
     * @return 200 with the updated equipment, 400 on validation errors, 404 if unknown, 409 if the name is taken
     */
    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable Long id, @Valid @RequestBody EquipmentRequest request) {
        return equipmentService.update(id, request);
    }

    /**
     * Deletes a piece of equipment.
     *
     * @param id the id of the equipment to delete
     * @return 204 with no body, or 404 if it does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}