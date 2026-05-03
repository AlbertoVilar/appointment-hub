package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import com.alberdev.study.appointmenthub.infrastructure.repositories.AppointmentRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            CustomerRepository customerRepository,
            ProfessionalRepository professionalRepository,
            ServiceOfferingRepository serviceOfferingRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.professionalRepository = professionalRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    public Appointment schedule(Long customerId, Long professionalId, Long serviceOfferingId,
                                LocalDateTime scheduledAt, String notes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Appointment> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment confirm(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment cancel(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment cancel(Long id, String reason) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment markAsDone(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment markAsNoShow(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Appointment reschedule(Long id, LocalDateTime newScheduledAt) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
