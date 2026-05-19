package com.alberdev.study.appointmenthub.api.dto;

import java.math.BigDecimal;

public record ServiceOfferingResponseDTO(

        Long id,
        String name,
        Integer durationInMinutes,
        BigDecimal basePrice,
        boolean active

) {
}
