package com.projet.auto_battler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.projet.auto_battler.entity.Equipment;
import com.projet.auto_battler.entity.Rarity;
import com.projet.auto_battler.entity.Slot;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.EquipmentRepository;
import com.projet.auto_battler.service.dto.EquipmentRequest;
import com.projet.auto_battler.service.dto.EquipmentResponse;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private EquipmentRequest request(String name) {
        EquipmentRequest request = new EquipmentRequest();
        request.setName(name);
        request.setSlot(Slot.values()[0]);
        request.setRarity(Rarity.COMMON);
        request.setBonusAttack(5);
        return request;
    }

    private Equipment equipment(Long id, String name) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(name);
        equipment.setSlot(Slot.values()[0]);
        equipment.setRarity(Rarity.COMMON);
        return equipment;
    }

    @Test
    void createSavesEquipmentWhenNameIsFree() {
        when(equipmentRepository.existsByNameIgnoreCase("Iron Sword")).thenReturn(false);
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(call -> {
            Equipment saved = call.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        EquipmentResponse response = equipmentService.create(request("  Iron Sword  "));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Iron Sword");
    }

    @Test
    void createFailsWhenNameAlreadyExists() {
        when(equipmentRepository.existsByNameIgnoreCase("Iron Sword")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> equipmentService.create(request("Iron Sword")));

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    void updateAllowsKeepingOwnName() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment(1L, "Iron Sword")));
        when(equipmentRepository.existsByNameIgnoreCaseAndIdNot("Iron Sword", 1L)).thenReturn(false);
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(call -> call.getArgument(0));

        EquipmentResponse response = equipmentService.update(1L, request("Iron Sword"));

        assertThat(response.name()).isEqualTo("Iron Sword");
    }

    @Test
    void updateFailsWhenNameBelongsToAnotherEquipment() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment(1L, "Iron Sword")));
        when(equipmentRepository.existsByNameIgnoreCaseAndIdNot("Steel Shield", 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> equipmentService.update(1L, request("Steel Shield")));

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    void findByIdFailsWhenEquipmentDoesNotExist() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipmentService.findById(99L));
    }

    @Test
    void deleteFailsWhenEquipmentDoesNotExist() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipmentService.delete(99L));

        verify(equipmentRepository, never()).delete(any());
    }

    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(equipmentRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(equipment(1L, "Iron Sword"), equipment(2L, "Steel Shield")));

        List<EquipmentResponse> result = equipmentService.findAll(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllWithSearchFiltersByName() {
        when(equipmentRepository.findByNameContainingIgnoreCase(any(String.class), any(Sort.class)))
                .thenReturn(List.of(equipment(1L, "Iron Sword")));

        List<EquipmentResponse> result = equipmentService.findAll("sword");

        assertThat(result).extracting(EquipmentResponse::name).containsExactly("Iron Sword");
    }
}