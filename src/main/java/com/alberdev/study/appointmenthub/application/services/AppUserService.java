package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.AppUser;
import com.alberdev.study.appointmenthub.infrastructure.repositories.AppUserRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;

    public AppUserService(AppUserRepository appUserRepository, RoleRepository roleRepository) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
    }

    public AppUser create(AppUser appUser) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AppUser findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<AppUser> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void enable(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void disable(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AppUser addRole(Long userId, Long roleId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AppUser removeRole(Long userId, Long roleId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
