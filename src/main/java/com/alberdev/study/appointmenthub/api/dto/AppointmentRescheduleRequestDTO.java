package com.alberdev.study.appointmenthub.api.dto;

import java.time.LocalDateTime;

public record AppointmentRescheduleRequestDTO(
        LocalDateTime scheduledAt
) {
}
