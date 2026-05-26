package com.alberdev.study.appointmenthub.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProfessionalRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String name,

        @NotBlank(message = "A especialidade é obrigatória")
        @Size(min = 3, max = 80, message = "A especialidade deve ter entre 3 e 80 caracteres")
        String specialty,

        @NotNull(message = "O profissional precisa estar vinculado a um usuario.")
        @Positive(message = "O id do usuário deve ser positivo")
        Long appUserId
) {
}
