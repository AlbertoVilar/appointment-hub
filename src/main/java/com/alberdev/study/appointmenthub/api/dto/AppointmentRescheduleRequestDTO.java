package com.alberdev.study.appointmenthub.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRescheduleRequestDTO(
        @NotNull(message = "A nova data do agendamento é obrigatória")
        @Future(message = "A data do agendamento nao pode estar no passado.")
        LocalDateTime scheduledAt
) {
}
