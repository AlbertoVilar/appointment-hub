package com.alberdev.study.appointmenthub.domain.entities;

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
}
