package com.alberdev.study.appointmenthub.api.mappers;

import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.ServiceOfferingResponseDTO;
import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import org.springframework.stereotype.Component;

@Component
public class ServiceOfferingMapper {

    public ServiceOffering toServiceOffering(ServiceOfferingRequestDTO requestDTO) {
        return new ServiceOffering(
                null,
                requestDTO.name(),
                requestDTO.durationInMinutes(),
                requestDTO.basePrice()
        );
    }

    public ServiceOfferingResponseDTO toServiceOfferingResponseDTO(ServiceOffering serviceOffering) {
        return new ServiceOfferingResponseDTO(
                serviceOffering.getId(),
                serviceOffering.getName(),
                serviceOffering.getDurationInMinutes(),
                serviceOffering.getBasePrice(),
                serviceOffering.isActive()
        );
    }

}
