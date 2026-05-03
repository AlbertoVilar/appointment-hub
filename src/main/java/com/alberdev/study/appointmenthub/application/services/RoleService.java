package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Role;
import com.alberdev.study.appointmenthub.domain.enums.RoleName;
import com.alberdev.study.appointmenthub.infrastructure.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role create(Role role) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Role findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Role findByName(RoleName name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Role> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
