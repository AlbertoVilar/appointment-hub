package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.enums.RoleName;

public final class RoleTestData {

    private RoleTestData() {
    }

    public static Role createProfessionalRole() {
        Role role = new Role();
        role.setName(RoleName.ROLE_PROFESSIONAL);
        return role;
    }
}
