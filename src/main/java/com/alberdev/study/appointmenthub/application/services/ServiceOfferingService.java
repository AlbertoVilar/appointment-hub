package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public ServiceOfferingService(ServiceOfferingRepository serviceOfferingRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    @Transactional
    public ServiceOffering create(ServiceOffering serviceOffering) {

        verifyIfNullServiceOffering(serviceOffering);

        if (serviceOfferingRepository.existsByName(serviceOffering.getName())) {
            throw new ResourceAlreadyExistsException("O serviço ja existe");
        }

        return serviceOfferingRepository.save(serviceOffering);

    }

    @Transactional(readOnly = true)
    public ServiceOffering findById(Long id) {

        verifyIfNullId(id);

        return serviceOfferingRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Servico nao encontrado com id = " + id)
                );
    }

    @Transactional(readOnly = true)
    public List<ServiceOffering> findAll() {

        return serviceOfferingRepository.findAll();
    }

    @Transactional
    public ServiceOffering updateServiceOffering(Long id, ServiceOffering input) {

        verifyIfNullId(id);
        verifyIfNullServiceOffering(input);

        var entity = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado com id = " + id));

        // Atualiza o nome quando informado
        Optional.ofNullable(input.getName())
                .ifPresent(entity::setName);

        // Atualiza o preco base usando a regra de dominio da entidade
        Optional.ofNullable(input.getBasePrice())
                .ifPresent(entity::updateBasePrice);

        // Atualiza a duracao usando a regra de dominio da entidade
        Optional.ofNullable(input.getDurationInMinutes())
                .ifPresent(entity::updateDuration);

        return serviceOfferingRepository.save(entity);
    }

    @Transactional
    public void activate(Long id) {

        verifyIfNullId(id);
        var entity = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado com id = " + id));

        entity.activate();

        serviceOfferingRepository.save(entity);
    }

    @Transactional
    public void deactivate(Long id) {

        verifyIfNullId(id);
        var entity = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado com id = " + id));

        entity.deactivate();

        serviceOfferingRepository.save(entity);
    }

    @Transactional
    public ServiceOffering updateBasePrice(Long id, BigDecimal newPrice) {

        verifyIfNullId(id);
        var entity = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado com id = " + id));

        entity.updateBasePrice(newPrice);

        return serviceOfferingRepository.save(entity);
    }

    @Transactional
    public ServiceOffering updateDuration(Long id, Integer newDurationInMinutes) {

        verifyIfNullId(id);
        var entity = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servico nao encontrado com id = " + id));

        entity.updateDuration(newDurationInMinutes);

        return serviceOfferingRepository.save(entity);
    }

    private void verifyIfNullId(Long id) {

        if (id == null) {
            throw new DomainException("O id nao pode ser nulo.");
        }
    }

    private void verifyIfNullServiceOffering(ServiceOffering serviceOffering) {

        if (serviceOffering == null) {
            throw new DomainException("O servico nao pode ser nulo.");
        }
    }
}
