package com.alberdev.study.appointmenthub.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceOfferingRequestDTO(

        @NotBlank(message = "O nome do serviço é obrigatório")
        @Size(min = 3, max = 80, message = "O nome do serviço deve ter entre 3 e 80 caracteres")
        String name,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        Integer durationInMinutes,

        @NotNull(message = "O preço base é obrigatório")
        @Positive(message = "O preço base deve ser maior que zero")
        BigDecimal basePrice

) {
}
