package com.alberdev.study.appointmenthub.domain.entities;

import java.util.HashSet;
import java.util.Set;

public final class AppUserTestData {

    private AppUserTestData() {
    }

    public static AppUser createValidAppUser() {
        return new AppUser(
                null,
                "ana.costa",
                "{noop}123456",
                true,
                new HashSet<>()
        );
    }
    public static AppUser createAdminUser() {
        // 1. Pegamos um usuário válido básico
        AppUser user = createValidAppUser();

        // 2. Usamos a OUTRA fábrica para criar um papel (Role)
        // Supondo que em RoleTestData você tenha um método para criar a role ADMIN
        Role adminRole = RoleTestData.createProfessionalRole();

        // 3. Usamos o seu método auxiliar para conectar os dois
        user.addRole(adminRole);

        return user;
    }

}
