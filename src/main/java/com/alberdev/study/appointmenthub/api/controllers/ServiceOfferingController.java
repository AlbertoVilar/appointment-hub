package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.ServiceOfferingMapper;
import com.alberdev.study.appointmenthub.application.services.ServiceOfferingService;
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
@RequestMapping(value = "/api/v1/service-offerings")
@Tag(name = "Service Offerings", description = "Operações para gerenciamento dos serviços oferecidos.")
public class ServiceOfferingController {

    private final ServiceOfferingService offeringService;
    private final ServiceOfferingMapper mapper;

    public ServiceOfferingController(ServiceOfferingService offeringService, ServiceOfferingMapper mapper) {
        this.offeringService = offeringService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido."),
            @ApiResponse(responseCode = "409", description = "Serviço já cadastrado.")
    })
    public ResponseEntity<ServiceOfferingResponseDTO> create(@RequestBody @Valid ServiceOfferingRequestDTO requestDTO) {
        var savedEntity = offeringService.create(mapper.toServiceOffering(requestDTO));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedEntity.getId())
                .toUri();

        return ResponseEntity.created(uri).body(mapper.toServiceOfferingResponseDTO(savedEntity));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Busca um serviço por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    public ResponseEntity<ServiceOfferingResponseDTO> findById(@PathVariable Long id) {
        var response = offeringService.findById(id);
        return ResponseEntity.ok(mapper.toServiceOfferingResponseDTO(response));
    }

    @GetMapping
    @Operation(summary = "Lista serviços de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviços listados com sucesso.")
    })
    public ResponseEntity<Page<ServiceOfferingResponseDTO>> findAll(Pageable pageable) {
        var serviceOfferings = offeringService.findAll(pageable);
        return ResponseEntity.ok(serviceOfferings.map(mapper::toServiceOfferingResponseDTO));
    }

    @PatchMapping(value = "/{id}")
    @Operation(summary = "Atualiza os dados de um serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado."),
            @ApiResponse(responseCode = "409", description = "Serviço já cadastrado.")
    })
    public ResponseEntity<ServiceOfferingResponseDTO> update(@PathVariable Long id,
                                                             @RequestBody @Valid ServiceOfferingRequestDTO requestDTO) {

        var serviceOffering = mapper.toServiceOfferingUpdate(requestDTO);
        var updatedServiceOffering = offeringService.updateServiceOffering(id, serviceOffering);
        return ResponseEntity.ok(mapper.toServiceOfferingResponseDTO(updatedServiceOffering));

    }

    @PatchMapping(value = "/{id}/activate")
    @Operation(summary = "Ativa um serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço ativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o estado atual do serviço."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        offeringService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/deactivate")
    @Operation(summary = "Desativa um serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço desativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o estado atual do serviço."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        offeringService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/base-price")
    @Operation(summary = "Atualiza o preço base de um serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preço base atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Preço base inválido."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    public ResponseEntity<ServiceOfferingResponseDTO> updateBasePrice(@PathVariable Long id,
                                                                      @RequestBody ServiceOfferingRequestDTO requestDTO) {
        return ResponseEntity.ok(
                mapper.toServiceOfferingResponseDTO(
                        offeringService.updateBasePrice(id, requestDTO.basePrice())
                )
        );

    }

    @PatchMapping(value = "/{id}/duration")
    @Operation(summary = "Atualiza a duração de um serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duração atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Duração inválida."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    public ResponseEntity<ServiceOfferingResponseDTO> updateDuration(@PathVariable Long id,
                                                                     @RequestBody ServiceOfferingRequestDTO requestDTO) {
        return ResponseEntity.ok(
                mapper.toServiceOfferingResponseDTO(
                        offeringService.updateDuration(id, requestDTO.durationInMinutes())
                )
        );
    }
}
