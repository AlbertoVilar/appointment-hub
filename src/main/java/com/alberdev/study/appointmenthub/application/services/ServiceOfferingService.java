package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public ServiceOfferingService(ServiceOfferingRepository serviceOfferingRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    public ServiceOffering create(ServiceOffering serviceOffering) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceOffering findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<ServiceOffering> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceOffering update(Long id, ServiceOffering serviceOffering) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void activate(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deactivate(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceOffering updateBasePrice(Long id, BigDecimal newPrice) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceOffering updateDuration(Long id, Integer newDurationInMinutes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
