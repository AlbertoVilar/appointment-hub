package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.AppointmentRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Appointment schedule(Long customerId, Long professionalId, Long serviceOfferingId,
                                LocalDateTime scheduledAt, String notes) {

        verifyIfNullId(customerId);
        verifyIfNullId(professionalId);
        verifyIfNullId(serviceOfferingId);

        var foundCustomer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Cliente nao encontrado com id " + customerId)
        );

        var foundProfessional = professionalRepository.findById(professionalId).orElseThrow(
                () -> new ResourceNotFoundException("Profissional nao encontrado com id " + professionalId)
        );

        var foundServiceOffering = serviceOfferingRepository.findById(serviceOfferingId).orElseThrow(
                () -> new ResourceNotFoundException("Servico nao encontrado com id " + serviceOfferingId)
        );

        Appointment appointment = new Appointment();
        appointment.assignCustomer(foundCustomer);
        appointment.assignProfessional(foundProfessional);
        appointment.assignServiceOffering(foundServiceOffering);

        appointment.scheduleAt(scheduledAt);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes(notes);

        return appointmentRepository.save(appointment);
    }


    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        verifyIfNullId(id);
        return findExistingAppointment(id);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Transactional
    public Appointment confirm(Long id) {

        verifyIfNullId(id);

        var appointment = findExistingAppointment(id);
        appointment.confirm();

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment cancel(Long id) {
        verifyIfNullId(id);

        var appointment = findExistingAppointment(id);
        appointment.cancel();

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment cancel(Long id, String reason) {
        verifyIfNullId(id);

        var appointment = findExistingAppointment(id);
        appointment.cancel(reason);

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment markAsDone(Long id) {
        verifyIfNullId(id);

        var appointment = findExistingAppointment(id);
        appointment.markAsDone();

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment markAsNoShow(Long id) {
        verifyIfNullId(id);

        var appointment = findExistingAppointment(id);
        appointment.markAsNoShow();

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment reschedule(Long id, LocalDateTime newScheduledAt) {
        verifyIfNullId(id);

        var appointment = findExistingAppointment(id);
        appointment.reschedule(newScheduledAt);

        return appointmentRepository.save(appointment);
    }

    private void verifyIfNullId(Long id) {

        if (id == null) {
            throw new DomainException("O id nao pode ser nulo.");
        }
    }

    private Appointment findExistingAppointment(Long id) {
        return appointmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Agendamento nao encontrado com id " + id)
        );
    }
}
