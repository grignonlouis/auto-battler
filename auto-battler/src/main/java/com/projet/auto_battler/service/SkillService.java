package com.projet.auto_battler.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projet.auto_battler.entity.Skill;
import com.projet.auto_battler.exception.BusinessRuleException;
import com.projet.auto_battler.exception.ResourceNotFoundException;
import com.projet.auto_battler.repository.SkillRepository;
import com.projet.auto_battler.service.dto.SkillRequest;
import com.projet.auto_battler.service.dto.SkillResponse;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> findAll(String search) {
        Sort sort = Sort.by("name");
        List<Skill> skills;
        if (search == null || search.isBlank()) {
            skills = skillRepository.findAll(sort);
        } else {
            skills = skillRepository.findByNameContainingIgnoreCase(search.trim(), sort);
        }
        return skills.stream().map(SkillResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse findById(Long id) {
        return SkillResponse.from(getEntityOrThrow(id));
    }

    @Transactional(readOnly = true)
    public SkillRequest getForEdit(Long id) {
        Skill skill = getEntityOrThrow(id);
        SkillRequest request = new SkillRequest();
        request.setName(skill.getName());
        request.setDescription(skill.getDescription());
        request.setSkillType(skill.getSkillType());
        request.setPower(skill.getPower());
        return request;
    }

    public SkillResponse create(SkillRequest request) {
        String name = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("A skill named \"" + name + "\" already exists.");
        }
        Skill skill = new Skill();
        copyToEntity(request, skill);
        return SkillResponse.from(skillRepository.save(skill));
    }

    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("A skill named \"" + name + "\" already exists.");
        }
        copyToEntity(request, skill);
        return SkillResponse.from(skillRepository.save(skill));
    }

    public void delete(Long id) {
        // Hero owns this relation and is not unlinked here, so delete fails once a hero has the skill.
        Skill skill = getEntityOrThrow(id);
        skillRepository.delete(skill);
    }

    @Transactional(readOnly = true)
    public Skill getEntityOrThrow(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    private void copyToEntity(SkillRequest request, Skill skill) {
        skill.setName(request.getName().trim());
        skill.setDescription(request.getDescription());
        skill.setSkillType(request.getSkillType());
        skill.setPower(request.getPower());
    }
}