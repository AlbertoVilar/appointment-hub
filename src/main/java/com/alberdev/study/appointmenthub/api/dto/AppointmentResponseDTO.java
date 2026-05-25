package com.alberdev.study.appointmenthub.api.dto;

import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        Long customerId,
        Long professionalId,
        Long serviceOfferingId,
        LocalDateTime scheduledAt,
        AppointmentStatus status,
        String notes,
        String cancelReason
) {
}
