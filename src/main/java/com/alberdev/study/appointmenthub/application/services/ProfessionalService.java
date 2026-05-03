package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Professional;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    public Professional create(Professional professional) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Professional findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Professional> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Professional update(Long id, Professional professional) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void activate(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deactivate(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Professional updateSpecialty(Long id, String specialty) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
