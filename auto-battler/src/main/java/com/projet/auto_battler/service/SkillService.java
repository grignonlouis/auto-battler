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

/**
 * Business logic for skills: search, creation, update, deletion and rule checks.
 * Controllers go through this class and never use the repository directly.
 * Entities are converted to DTOs here, while the transaction is still open.
 */
@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    /**
     * Creates the service with its repository.
     *
     * @param skillRepository the repository used to access skills
     */
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Returns the skills sorted by name, optionally filtered by a search text.
     *
     * @param search text that must be contained in the name, or null/blank for all skills
     * @return the matching skills
     */
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

    /**
     * Returns one skill by its id.
     *
     * @param id the skill id
     * @return the skill
     * @throws ResourceNotFoundException if no skill has this id
     */
    @Transactional(readOnly = true)
    public SkillResponse findById(Long id) {
        return SkillResponse.from(getEntityOrThrow(id));
    }

    /**
     * Builds the data used to pre-fill the edit form.
     *
     * @param id the skill id
     * @return a request object holding the current values of the skill
     * @throws ResourceNotFoundException if no skill has this id
     */
    @Transactional(readOnly = true)
    public SkillRequest getForEdit(Long id) {
        Skill skill = getEntityOrThrow(id);
        SkillRequest request = new SkillRequest();
        request.setName(skill.getName());
        request.setDescription(skill.getDescription());
        request.setSkillType(skill.getSkillType());
        request.setPower(skill.getPower());
        request.setCooldown(skill.getCooldown());
        request.setDuration(skill.getDuration());
        return request;
    }

    /**
     * Creates a new skill.
     * The name must be unique, ignoring case.
     *
     * @param request the data of the new skill
     * @return the created skill
     * @throws BusinessRuleException if the name is already used
     */
    public SkillResponse create(SkillRequest request) {
        String name = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException("A skill named \"" + name + "\" already exists.");
        }
        Skill skill = new Skill();
        copyToEntity(request, skill);
        return SkillResponse.from(skillRepository.save(skill));
    }

    /**
     * Updates an existing skill.
     * The new name must not be used by another skill, but the skill may keep its own name.
     *
     * @param id      the id of the skill to update
     * @param request the new data
     * @return the updated skill
     * @throws ResourceNotFoundException if no skill has this id
     * @throws BusinessRuleException     if another skill already uses the name
     */
    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = getEntityOrThrow(id);
        String name = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessRuleException("A skill named \"" + name + "\" already exists.");
        }
        copyToEntity(request, skill);
        return SkillResponse.from(skillRepository.save(skill));
    }

    /**
     * Deletes a skill.
     * Once the Hero entity exists, this method must first remove the skill from every hero,
     * because Hero owns the relation and the database would refuse the deletion otherwise.
     *
     * @param id the id of the skill to delete
     * @throws ResourceNotFoundException if no skill has this id
     */
    public void delete(Long id) {
        Skill skill = getEntityOrThrow(id);
        skillRepository.delete(skill);
    }

    /**
     * Returns the Skill entity itself, not a DTO.
     * Intended for other services (for example the hero service); controllers must not use it.
     *
     * @param id the skill id
     * @return the skill entity
     * @throws ResourceNotFoundException if no skill has this id
     */
    @Transactional(readOnly = true)
    public Skill getEntityOrThrow(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    /**
     * Copies the fields of a request into an entity, trimming the name.
     * Shared by create and update.
     *
     * @param request the source data
     * @param skill   the entity to fill
     */
    private void copyToEntity(SkillRequest request, Skill skill) {
        skill.setName(request.getName().trim());
        skill.setDescription(request.getDescription());
        skill.setSkillType(request.getSkillType());
        skill.setPower(request.getPower());
        skill.setCooldown(request.getCooldown());
        skill.setDuration(request.getDuration());
    }
}