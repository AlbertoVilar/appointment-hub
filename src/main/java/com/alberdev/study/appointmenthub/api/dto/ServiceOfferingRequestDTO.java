package com.alberdev.study.appointmenthub.api.dto;

import java.math.BigDecimal;

public record ServiceOfferingRequestDTO(

        String name,
        Integer durationInMinutes,
        BigDecimal basePrice

) {
}
