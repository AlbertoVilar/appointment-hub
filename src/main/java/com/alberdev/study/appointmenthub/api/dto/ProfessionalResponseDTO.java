package com.alberdev.study.appointmenthub.api.dto;

public record ProfessionalResponseDTO(
        Long id,
        String name,
        String specialty,
        boolean active,
        Long appUserId
) {
}
