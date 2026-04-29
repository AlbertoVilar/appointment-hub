package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

import static com.alberdev.study.appointmenthub.domain.entities.AppUserTestData.createValidAppUser;
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

}
