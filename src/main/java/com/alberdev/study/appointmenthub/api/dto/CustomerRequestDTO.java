package com.alberdev.study.appointmenthub.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio ou nulo")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Digite um e-mail válido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter apenas números e ter 10 ou 11 dígitos (com DDD)")
        String phone

) {
}
