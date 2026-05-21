package com.alberdev.study.appointmenthub.api.dto;

public record ProfessionalRequestDTO(
        String name,
        String specialty,
        Long appUserId
) {
}
