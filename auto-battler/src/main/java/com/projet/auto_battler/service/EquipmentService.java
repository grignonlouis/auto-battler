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

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

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

    @Transactional(readOnly = true)
    public EquipmentResponse findById(Long id) {
        return EquipmentResponse.from(getEntityOrThrow(id));
    }

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

    public EquipmentResponse create(EquipmentRequest request) {
        String name = request.getName().trim();
        if (equipmentRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("An equipment named \"" + name + "\" already exists.");
        }
        Equipment equipment = new Equipment();
        copyToEntity(request, equipment);
        return EquipmentResponse.from(equipmentRepository.save(equipment));
    }

    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment equipment = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (equipmentRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("An equipment named \"" + name + "\" already exists.");
        }
        copyToEntity(request, equipment);
        return EquipmentResponse.from(equipmentRepository.save(equipment));
    }

    public void delete(Long id) {
        // Hero owns this relation and is not unlinked here, so delete fails once a hero wears the item.
        Equipment equipment = getEntityOrThrow(id);
        equipmentRepository.delete(equipment);
    }

    @Transactional(readOnly = true)
    public Equipment getEntityOrThrow(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id));
    }

    private void copyToEntity(EquipmentRequest request, Equipment equipment) {
        equipment.setName(request.getName().trim());
        equipment.setSlot(request.getSlot());
        equipment.setRarity(request.getRarity());
        equipment.setBonusAttack(request.getBonusAttack());
        equipment.setBonusHealth(request.getBonusHealth());
        equipment.setBonusDefense(request.getBonusDefense());
    }
}