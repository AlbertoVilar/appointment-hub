package com.alberdev.study.appointmenthub.api.mappers;

import com.alberdev.study.appointmenthub.api.dto.AppointmentResponseDTO;
import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getCustomer() != null ? appointment.getCustomer().getId() : null,
                appointment.getProfessional() != null ? appointment.getProfessional().getId() : null,
                appointment.getServiceOffering() != null ? appointment.getServiceOffering().getId() : null,
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCancelReason()
        );
    }
}
