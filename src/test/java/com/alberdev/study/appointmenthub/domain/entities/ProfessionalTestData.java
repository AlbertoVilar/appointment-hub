package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.api.dto.ProfessionalRequestDTO;

public final class ProfessionalTestData {

    private ProfessionalTestData() {
    }

    public static Professional createValidProfessional() {
        Professional professional = new Professional();
        professional.setName("Dra. Ana Costa");
        professional.setSpecialty("Fisioterapia");
        professional.setActive(true);
        professional.setAppUser(AppUserTestData.createValidAppUser());
        return professional;
    }

    public static ProfessionalRequestDTO createProfessionalRequestDTO() {
        return new ProfessionalRequestDTO(
                "Dra. Ana Costa",
                "Fisioterapia",
                1L
        );
    }

}
