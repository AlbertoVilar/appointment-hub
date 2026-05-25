package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.AppointmentCancelRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentRescheduleRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.AppointmentMapper;
import com.alberdev.study.appointmenthub.application.services.AppointmentService;
import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper mapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentMapper mapper) {
        this.appointmentService = appointmentService;
        this.mapper = mapper;
    }

    @PostMapping
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
    public ResponseEntity<Page<AppointmentResponseDTO>> findAll(Pageable pageable) {
        Page<Appointment> appointments = appointmentService.findAll(pageable);
        return ResponseEntity.ok(appointments.map(mapper::toAppointmentResponseDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id) {
        Appointment appointment = appointmentService.findById(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirm(@PathVariable Long id) {
        Appointment appointment = appointmentService.confirm(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/cancel")
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
    public ResponseEntity<AppointmentResponseDTO> reschedule(
            @PathVariable Long id,
            @RequestBody @Valid AppointmentRescheduleRequestDTO requestDTO
    ) {
        Appointment appointment = appointmentService.reschedule(id, requestDTO.scheduledAt());
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/done")
    public ResponseEntity<AppointmentResponseDTO> markAsDone(@PathVariable Long id) {
        Appointment appointment = appointmentService.markAsDone(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }

    @PatchMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponseDTO> markAsNoShow(@PathVariable Long id) {
        Appointment appointment = appointmentService.markAsNoShow(id);
        return ResponseEntity.ok(mapper.toAppointmentResponseDTO(appointment));
    }
}
