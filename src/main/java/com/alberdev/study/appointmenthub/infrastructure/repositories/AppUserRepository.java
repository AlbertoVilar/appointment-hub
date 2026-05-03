package com.alberdev.study.appointmenthub.infrastructure.repositories;

import com.alberdev.study.appointmenthub.domain.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
