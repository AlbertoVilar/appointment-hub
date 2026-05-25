package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.api.dto.AppointmentRequestDTO;
import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

import static com.alberdev.study.appointmenthub.domain.entities.CustomerTestData.createValidCustomer;
import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createValidProfessional;
import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createValidServiceOffering;

public final class AppointmentTestData {

    private AppointmentTestData() {
    }

    public static Appointment createValidScheduledAppointment() {
        Appointment appointment = new Appointment();
        appointment.setCustomer(createValidCustomer());
        appointment.setProfessional(createValidProfessional());
        appointment.setServiceOffering(createValidServiceOffering());
        appointment.setScheduledAt(LocalDateTime.now().plusDays(1).withNano(0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes("Primeiro atendimento");
        return appointment;
    }

    public static Appointment createValidSchedulableAppointment() {
        Appointment appointment = new Appointment();
        appointment.assignCustomer(createValidCustomer());
        appointment.assignProfessional(createValidProfessional());
        appointment.assignServiceOffering(createValidServiceOffering());
        appointment.scheduleAt(LocalDateTime.now().plusDays(1).withNano(0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes("Primeiro atendimento");
        return appointment;
    }

    public static AppointmentRequestDTO createAppointmentRequestDTO() {
        return new AppointmentRequestDTO(
                1L,
                1L,
                1L,
                LocalDateTime.of(2030, 5, 25, 14, 30),
                "Agendamento Teste"
        );
    }

    public static Appointment createValidScheduledAppointmentWithIds() {
        Appointment appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.getCustomer().setId(1L);
        appointment.getProfessional().setId(1L);
        appointment.getServiceOffering().setId(1L);
        appointment.setScheduledAt(LocalDateTime.of(2030, 5, 25, 14, 30));
        return appointment;
    }

}
