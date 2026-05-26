package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.AppointmentCancelRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentRescheduleRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.AppointmentMapper;
import com.alberdev.study.appointmenthub.application.services.AppointmentService;
import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/appointments")
@Tag(name = "Appointments", description = "Operações para gerenciamento de agendamentos.")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper mapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentMapper mapper) {
        this.appointmentService = appointmentService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Agenda um novo atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de agendamento violada."),
            @ApiResponse(responseCode = "404", description = "Cliente, profissional ou serviço não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> create(@RequestBody @Valid AppointmentRequestDTO requestDTO) {

        Appointment appointment = appointmentService.schedule(
                requestDTO.customerId(),
                requestDTO.professionalId(),
                requestDTO.serviceOfferingId(),
                requestDTO.scheduledAt(),
                requestDTO.notes()
        );

        AppointmentResponseDTO responseDTO = mapper.toAppointmentResponseDTO(appointment);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(appointment.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista agendamentos de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamentos listados com sucesso.")
    })
    public ResponseEntity<Page<AppointmentResponseDTO>> findAll(Pageable pageable) {
        Page<Appointment> appointments = appointmentService.findAll(pageable);
        return ResponseEntity.ok(appointments.map(mapper::toAppointmentResponseDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um agendamento por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id) {
        Appointment appointment = appointmentService.findById(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirma um agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento confirmado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o status atual do agendamento."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> confirm(@PathVariable Long id) {
        Appointment appointment = appointmentService.confirm(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancela um agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou operação inválida para o status atual."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid AppointmentCancelRequestDTO requestDTO
    ) {
        Appointment appointment;
        if (requestDTO != null && requestDTO.reason() != null && !requestDTO.reason().isBlank()) {
            appointment = appointmentService.cancel(id, requestDTO.reason());
        } else {
            appointment = appointmentService.cancel(id);
        }
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Reagenda um atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento reagendado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou operação inválida para o status atual."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> reschedule(
            @PathVariable Long id,
            @RequestBody @Valid AppointmentRescheduleRequestDTO requestDTO
    ) {
        Appointment appointment = appointmentService.reschedule(id, requestDTO.scheduledAt());
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/done")
    @Operation(summary = "Marca um agendamento como concluído")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento concluído com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o status atual do agendamento."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> markAsDone(@PathVariable Long id) {
        Appointment appointment = appointmentService.markAsDone(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/no-show")
    @Operation(summary = "Marca falta em um agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Falta registrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o status atual do agendamento."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado.")
    })
    public ResponseEntity<AppointmentResponseDTO> markAsNoShow(@PathVariable Long id) {
        Appointment appointment = appointmentService.markAsNoShow(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }
}
