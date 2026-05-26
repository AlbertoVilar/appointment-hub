package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.ProfessionalRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.ProfessionalResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.ProfessionalMapper;
import com.alberdev.study.appointmenthub.application.services.ProfessionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Professionals", description = "Operações para gerenciamento de profissionais.")
public class ProfessionalController {

    private final ProfessionalService service;
    private final ProfessionalMapper mapper;

    public ProfessionalController(ProfessionalService service, ProfessionalMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido."),
            @ApiResponse(responseCode = "409", description = "Usuário já vinculado a outro profissional.")
    })
    public ResponseEntity<ProfessionalResponseDTO> create(@RequestBody @Valid ProfessionalRequestDTO requestDTO) {
        var savedProfessional = service.create(mapper.toProfessional(requestDTO));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProfessional.getId())
                .toUri();

        return ResponseEntity.created(uri).body(mapper.toProfessionalResponseDTO(savedProfessional));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Busca um profissional por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    public ResponseEntity<ProfessionalResponseDTO> findById(@PathVariable Long id) {
        var professional = service.findById(id);
        return ResponseEntity.ok(mapper.toProfessionalResponseDTO(professional));
    }

    @GetMapping
    @Operation(summary = "Lista profissionais de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissionais listados com sucesso.")
    })
    public ResponseEntity<Page<ProfessionalResponseDTO>> findAll(Pageable pageable) {
        var professionals = service.findAll(pageable);
        return ResponseEntity.ok(professionals.map(mapper::toProfessionalResponseDTO));
    }

    @PatchMapping(value = "/{id}")
    @Operation(summary = "Atualiza os dados de um profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado."),
            @ApiResponse(responseCode = "409", description = "Usuário já vinculado a outro profissional.")
    })
    public ResponseEntity<ProfessionalResponseDTO> update(@PathVariable Long id,
                                                          @RequestBody @Valid ProfessionalRequestDTO requestDTO) {
        var professionalUpdate = mapper.toProfessionalUpdate(requestDTO);
        var updatedProfessional = service.update(id, professionalUpdate);
        return ResponseEntity.ok(mapper.toProfessionalResponseDTO(updatedProfessional));
    }

    @PatchMapping(value = "/{id}/activate")
    @Operation(summary = "Ativa um profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profissional ativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o estado atual do profissional."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/deactivate")
    @Operation(summary = "Desativa um profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profissional desativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o estado atual do profissional."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/specialty")
    @Operation(summary = "Atualiza a especialidade de um profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialidade atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Especialidade inválida."),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    })
    public ResponseEntity<ProfessionalResponseDTO> updateSpecialty(@PathVariable Long id,
                                                                   @RequestBody ProfessionalRequestDTO requestDTO) {
        var updatedProfessional = service.updateSpecialty(id, requestDTO.specialty());
        return ResponseEntity.ok(mapper.toProfessionalResponseDTO(updatedProfessional));
    }
}
