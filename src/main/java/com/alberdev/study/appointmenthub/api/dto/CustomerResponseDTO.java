package com.alberdev.study.appointmenthub.api.dto;

public record CustomerResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        boolean active
) {
}
