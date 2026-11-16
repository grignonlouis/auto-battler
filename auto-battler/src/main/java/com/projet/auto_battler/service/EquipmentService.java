package com.projet.auto_battler.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projet.auto_battler.entity.Equipment;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.EquipmentRepository;
import com.projet.auto_battler.service.dto.EquipmentRequest;
import com.projet.auto_battler.service.dto.EquipmentResponse;

/**
 * Business logic for equipment: search, creation, update, deletion and rule checks.
 * Controllers go through this class and never use the repository directly.
 */
@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    /**
     * Creates the service with its repository.
     *
     * @param equipmentRepository the repository used to access equipment
     */
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Returns the equipment sorted by name, optionally filtered by a search text.
     *
     * @param search text that must be contained in the name, or null/blank for everything
     * @return the matching equipment
     */
    @Transactional(readOnly = true)
    public List<EquipmentResponse> findAll(String search) {
        Sort sort = Sort.by("name");
        List<Equipment> equipment;
        if (search == null || search.isBlank()) {
            equipment = equipmentRepository.findAll(sort);
        } else {
            equipment = equipmentRepository.findByNameContainingIgnoreCase(search.trim(), sort);
        }
        return equipment.stream().map(EquipmentResponse::from).toList();
    }

    /**
     * Returns one piece of equipment by its id.
     *
     * @param id the equipment id
     * @return the equipment
     * @throws ResourceNotFoundException if no equipment has this id
     */
    @Transactional(readOnly = true)
    public EquipmentResponse findById(Long id) {
        return EquipmentResponse.from(getEntityOrThrow(id));
    }

    /**
     * Builds the data used to pre-fill the edit form.
     *
     * @param id the equipment id
     * @return a request object holding the current values of the equipment
     * @throws ResourceNotFoundException if no equipment has this id
     */
    @Transactional(readOnly = true)
    public EquipmentRequest getForEdit(Long id) {
        Equipment equipment = getEntityOrThrow(id);
        EquipmentRequest request = new EquipmentRequest();
        request.setName(equipment.getName());
        request.setSlot(equipment.getSlot());
        request.setRarity(equipment.getRarity());
        request.setBonusAttack(equipment.getBonusAttack());
        request.setBonusHealth(equipment.getBonusHealth());
        request.setBonusDefense(equipment.getBonusDefense());
        return request;
    }

    /**
     * Creates a new piece of equipment.
     * The name must be unique, ignoring case.
     *
     * @param request the data of the new equipment
     * @return the created equipment
     * @throws BusinessRuleException if the name is already used
     */
    public EquipmentResponse create(EquipmentRequest request) {
        String name = request.getName().trim();
        if (equipmentRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("An equipment named \"" + name + "\" already exists.");
        }
        Equipment equipment = new Equipment();
        copyToEntity(request, equipment);
        return EquipmentResponse.from(equipmentRepository.save(equipment));
    }

    /**
     * Updates an existing piece of equipment.
     * The new name must not be used by another piece of equipment, but it may keep its own name.
     *
     * @param id      the id of the equipment to update
     * @param request the new data
     * @return the updated equipment
     * @throws ResourceNotFoundException if no equipment has this id
     * @throws BusinessRuleException     if another piece of equipment already uses the name
     */
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment equipment = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (equipmentRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("An equipment named \"" + name + "\" already exists.");
        }
        copyToEntity(request, equipment);
        return EquipmentResponse.from(equipmentRepository.save(equipment));
    }

    /**
     * Deletes a piece of equipment.
     * Once the Hero entity exists, this method must first remove the equipment from every hero,
     * because Hero owns the relation and the database would refuse the deletion otherwise.
     *
     * @param id the id of the equipment to delete
     * @throws ResourceNotFoundException if no equipment has this id
     */
    public void delete(Long id) {
        Equipment equipment = getEntityOrThrow(id);
        equipmentRepository.delete(equipment);
    }

    /**
     * Returns the Equipment entity itself, not a DTO.
     * Intended for other services (for example the hero service); controllers must not use it.
     *
     * @param id the equipment id
     * @return the equipment entity
     * @throws ResourceNotFoundException if no equipment has this id
     */
    @Transactional(readOnly = true)
    public Equipment getEntityOrThrow(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id));
    }

    /**
     * Copies the fields of a request into an entity, trimming the name.
     * Shared by create and update.
     *
     * @param request   the source data
     * @param equipment the entity to fill
     */
    private void copyToEntity(EquipmentRequest request, Equipment equipment) {
        equipment.setName(request.getName().trim());
        equipment.setSlot(request.getSlot());
        equipment.setRarity(request.getRarity());
        equipment.setBonusAttack(request.getBonusAttack());
        equipment.setBonusHealth(request.getBonusHealth());
        equipment.setBonusDefense(request.getBonusDefense());
    }
}