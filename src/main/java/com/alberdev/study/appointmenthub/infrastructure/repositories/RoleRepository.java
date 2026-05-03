package com.alberdev.study.appointmenthub.infrastructure.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
