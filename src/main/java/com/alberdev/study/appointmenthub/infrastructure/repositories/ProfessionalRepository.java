package com.alberdev.study.appointmenthub.infrastructure.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
}
