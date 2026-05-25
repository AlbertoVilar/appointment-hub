package com.alberdev.study.appointmenthub.api.dto;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        Long customerId,
        Long professionalId,
        Long serviceOfferingId,
        LocalDateTime scheduledAt,
        String notes
) {
}
