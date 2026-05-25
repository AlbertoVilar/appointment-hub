package com.alberdev.study.appointmenthub.api.dto;

import jakarta.validation.constraints.Size;

public record AppointmentCancelRequestDTO(
        @Size(max = 500, message = "O motivo do cancelamento deve ter no máximo 500 caracteres")
        String reason
) {
}
