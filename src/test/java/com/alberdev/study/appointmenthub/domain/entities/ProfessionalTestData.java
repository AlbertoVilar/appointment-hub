package com.alberdev.study.appointmenthub.domain.entities;

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
}
