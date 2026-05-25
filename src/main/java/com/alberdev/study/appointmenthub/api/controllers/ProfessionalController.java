package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.ProfessionalRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.ProfessionalResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.ProfessionalMapper;
import com.alberdev.study.appointmenthub.application.services.ProfessionalService;
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
@RequestMapping(value = "/api/v1/professionals")
public class ProfessionalController {

    private final ProfessionalService service;
    private final ProfessionalMapper mapper;

    public ProfessionalController(ProfessionalService service, ProfessionalMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ProfessionalResponseDTO> create(@RequestBody @Valid ProfessionalRequestDTO requestDTO) {
        var savedProfessional = service.create(mapper.toProfessional(requestDTO));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProfessional.getId())
                .toUri();

        return ResponseEntity.created(uri).body(mapper.toProfessionalResponseDTO(savedProfessional));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProfessionalResponseDTO> findById(@PathVariable Long id) {
        var professional = service.findById(id);
        return ResponseEntity.ok(mapper.toProfessionalResponseDTO(professional));
    }

    @GetMapping
    public ResponseEntity<Page<ProfessionalResponseDTO>> findAll(Pageable pageable) {
        var professionals = service.findAll(pageable);
        return ResponseEntity.ok(professionals.map(mapper::toProfessionalResponseDTO));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<ProfessionalResponseDTO> update(@PathVariable Long id,
                                                          @RequestBody @Valid ProfessionalRequestDTO requestDTO) {
        var professionalUpdate = mapper.toProfessionalUpdate(requestDTO);
        var updatedProfessional = service.update(id, professionalUpdate);
        return ResponseEntity.ok(mapper.toProfessionalResponseDTO(updatedProfessional));
    }

    @PatchMapping(value = "/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/specialty")
    public ResponseEntity<ProfessionalResponseDTO> updateSpecialty(@PathVariable Long id,
                                                                   @RequestBody ProfessionalRequestDTO requestDTO) {
        var updatedProfessional = service.updateSpecialty(id, requestDTO.specialty());
        return ResponseEntity.ok(mapper.toProfessionalResponseDTO(updatedProfessional));
    }
}
