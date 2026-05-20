package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingRequestDTO;

import java.math.BigDecimal;

public final class ServiceOfferingTestData {

    private ServiceOfferingTestData() {
    }

    public static ServiceOffering createValidServiceOffering() {
        ServiceOffering serviceOffering = new ServiceOffering();
        serviceOffering.setName("Consulta inicial");
        serviceOffering.setDurationInMinutes(60);
        serviceOffering.setBasePrice(new BigDecimal("150.00"));
        serviceOffering.setActive(true);
        return serviceOffering;
    }

    public static ServiceOfferingRequestDTO createServiceOfferingRequestDTO() {
        var requestDTO = new ServiceOfferingRequestDTO(
                "Odontologia",
                45,
                new BigDecimal("200.00")
        );
        return requestDTO;
    }
}
