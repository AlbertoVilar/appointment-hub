package com.alberdev.study.appointmenthub.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        @NotNull(message = "O id do cliente é obrigatório")
        @Positive(message = "O id do cliente deve ser positivo")
        Long customerId,

        @NotNull(message = "O id do profissional é obrigatório")
        @Positive(message = "O id do profissional deve ser positivo")
        Long professionalId,

        @NotNull(message = "O id do serviço é obrigatório")
        @Positive(message = "O id do serviço deve ser positivo")
        Long serviceOfferingId,

        @NotNull(message = "A data do agendamento é obrigatória")
        @Future(message = "A data do agendamento deve estar no futuro")
        LocalDateTime scheduledAt,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String notes
) {
}
