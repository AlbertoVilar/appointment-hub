package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.ServiceOfferingMapper;
import com.alberdev.study.appointmenthub.application.services.ServiceOfferingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/service-offerings")
public class ServiceOfferingController {

    private final ServiceOfferingService offeringService;
    private final ServiceOfferingMapper mapper;

    public ServiceOfferingController(ServiceOfferingService offeringService, ServiceOfferingMapper mapper) {
        this.offeringService = offeringService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ServiceOfferingResponseDTO> create(@RequestBody @Valid ServiceOfferingRequestDTO requestDTO) {
        var savedEntity = offeringService.create(mapper.toServiceOffering(requestDTO));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedEntity.getId())
                .toUri();

        return ResponseEntity.created(uri).body(mapper.toServiceOfferingResponseDTO(savedEntity));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ServiceOfferingResponseDTO> findById(@PathVariable Long id) {
        var response = offeringService.findById(id);
        return ResponseEntity.ok(mapper.toServiceOfferingResponseDTO(response));
    }

    @GetMapping
    public ResponseEntity<Page<ServiceOfferingResponseDTO>> findAll(Pageable pageable) {
        var serviceOfferings = offeringService.findAll(pageable);
        return ResponseEntity.ok(serviceOfferings.map(mapper::toServiceOfferingResponseDTO));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<ServiceOfferingResponseDTO> update(@PathVariable Long id,
                                                             @RequestBody @Valid ServiceOfferingRequestDTO requestDTO) {

        var serviceOffering = mapper.toServiceOfferingUpdate(requestDTO);
        var updatedServiceOffering = offeringService.updateServiceOffering(id, serviceOffering);
        return ResponseEntity.ok(mapper.toServiceOfferingResponseDTO(updatedServiceOffering));

    }

    @PatchMapping(value = "/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        offeringService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        offeringService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/base-price")
    public ResponseEntity<ServiceOfferingResponseDTO> updateBasePrice(@PathVariable Long id,
                                                                      @RequestBody ServiceOfferingRequestDTO requestDTO) {
        return ResponseEntity.ok(
                mapper.toServiceOfferingResponseDTO(
                        offeringService.updateBasePrice(id, requestDTO.basePrice())
                )
        );

    }

    @PatchMapping(value = "/{id}/duration")
    public ResponseEntity<ServiceOfferingResponseDTO> updateDuration(@PathVariable Long id,
                                                                     @RequestBody ServiceOfferingRequestDTO requestDTO) {
        return ResponseEntity.ok(
                mapper.toServiceOfferingResponseDTO(
                        offeringService.updateDuration(id, requestDTO.durationInMinutes())
                )
        );
    }
}
