package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Professional;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @Transactional
    public Professional create(Professional professional) {
        verifyIfNullProfessional(professional);
        verifyIfProfessionalHasNoAppUser(professional);

        if (professionalRepository.existsByAppUser(professional.getAppUser())) {
            throw new ResourceAlreadyExistsException("Ja existe um profissional cadastrado com esse usuario.");
        }

        professional.setActive(true);

        return professionalRepository.save(professional);
    }

    @Transactional(readOnly = true)
    public Professional findById(Long id) {

        verifyIfNullId(id);

        return professionalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profissional nao encontrado com id = " + id)
        );
    }

    @Transactional(readOnly = true)
    public Page<Professional> findAll(Pageable pageable) {
        return professionalRepository.findAll(pageable);
    }

    //UPDATE
    @Transactional
    public Professional update(Long id, Professional professionalUpdate) {

        verifyIfNullId(id);
        verifyIfNullProfessional(professionalUpdate);

        var professional = professionalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profissional nao encontrado com Id " + id)
        );

        professional.setName(professionalUpdate.getName() != null ? professionalUpdate.getName() : professional.getName());
        professional.setSpecialty(professionalUpdate.getSpecialty() != null ? professionalUpdate.getSpecialty() : professional.getSpecialty());
        professional.setAppUser(professionalUpdate.getAppUser() != null ? professionalUpdate.getAppUser() : professional.getAppUser());

        return professionalRepository.save(professional);
    }

    @Transactional
    public void activate(Long id) {
        verifyIfNullId(id);

        var professional = professionalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profissional nao encontrado com Id " + id)
        );

        professional.activate();

        professionalRepository.save(professional);
    }


    @Transactional
    public void deactivate(Long id) {
        verifyIfNullId(id);

        var professional = professionalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profissional nao encontrado com Id " + id)
        );

        professional.deactivate();

        professionalRepository.save(professional);
    }

    @Transactional
    public Professional updateSpecialty(Long id, String specialty) {

        verifyIfNullId(id);

        var professional = professionalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Profissional nao encontrado com Id " + id)
        );

        professional.updateSpecialty(specialty);

        return professionalRepository.save(professional);

    }

    private void verifyIfNullId(Long id) {

        if (id == null) {
            throw new DomainException("O id nao pode ser nulo.");
        }
    }

    private void verifyIfNullProfessional(Professional professional) {

        if (professional == null) {
            throw new DomainException("O profissional nao pode ser nulo.");
        }
    }

    private void verifyIfProfessionalHasNoAppUser(Professional professional) {

        if (professional.getAppUser() == null) {
            throw new DomainException("O profissional precisa estar vinculado a um usuario.");
        }
    }
}
