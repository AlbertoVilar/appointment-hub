package com.alberdev.study.appointmenthub.api.mappers;

import com.alberdev.study.appointmenthub.api.dto.ProfessionalRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.ProfessionalResponseDTO;
import com.alberdev.study.appointmenthub.domain.entities.AppUser;
import com.alberdev.study.appointmenthub.domain.entities.Professional;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalMapper {

    public Professional toProfessional(ProfessionalRequestDTO requestDTO) {
        return new Professional(
                null,
                requestDTO.name(),
                requestDTO.specialty(),
                true,
                toAppUserReference(requestDTO.appUserId())
        );
    }

    public Professional toProfessionalUpdate(ProfessionalRequestDTO requestDTO) {
        Professional professional = new Professional();
        professional.setName(requestDTO.name());
        professional.setSpecialty(requestDTO.specialty());
        professional.setAppUser(toAppUserReference(requestDTO.appUserId()));
        return professional;
    }

    public ProfessionalResponseDTO toProfessionalResponseDTO(Professional professional) {
        return new ProfessionalResponseDTO(
                professional.getId(),
                professional.getName(),
                professional.getSpecialty(),
                professional.isActive(),
                professional.getAppUser() != null ? professional.getAppUser().getId() : null
        );
    }

    private AppUser toAppUserReference(Long appUserId) {
        if (appUserId == null) {
            return null;
        }

        AppUser appUser = new AppUser();
        appUser.setId(appUserId);
        return appUser;
    }
}
