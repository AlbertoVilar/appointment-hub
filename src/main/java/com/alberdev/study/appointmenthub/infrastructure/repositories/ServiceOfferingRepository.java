package com.alberdev.study.appointmenthub.infrastructure.repositories;

import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
}
