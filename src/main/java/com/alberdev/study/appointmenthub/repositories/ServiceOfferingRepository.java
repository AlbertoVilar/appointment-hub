package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
}
