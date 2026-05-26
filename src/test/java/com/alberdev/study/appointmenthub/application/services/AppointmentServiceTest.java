package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.AppointmentRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.alberdev.study.appointmenthub.domain.entities.AppointmentTestData.createValidScheduledAppointment;
import static com.alberdev.study.appointmenthub.domain.entities.CustomerTestData.createValidCustomer;
import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createValidProfessional;
import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createValidServiceOffering;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private ServiceOfferingRepository serviceOfferingRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    // =========================
    // Schedule appointment
    // =========================

    @Test
    @DisplayName("Deve agendar quando cliente, profissional, servico e data forem validos")
    void shouldScheduleAppointmentWhenDataIsValid() {
        // 1. Arrange: criar Customer, Professional e ServiceOffering validos e ativos
        var customer = createValidCustomer();
        customer.setId(1L);

        var professional = createValidProfessional();
        professional.setId(1L);

        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);

        // 2. Arrange: definir uma data futura para o agendamento
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";


        // 3. Arrange: simular customerRepository.findById retornando o Customer
        Mockito.when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        // 4. Arrange: simular professionalRepository.findById retornando o Professional
        Mockito.when(professionalRepository.findById(professional.getId()))
                .thenReturn(Optional.of(professional));

        // 5. Arrange: simular serviceOfferingRepository.findById retornando o ServiceOffering
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId()))
                .thenReturn(Optional.of(serviceOffering));

        // 6. Arrange: simular appointmentRepository.save retornando o Appointment salvo
        Mockito.when(appointmentRepository.save(Mockito.any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 7. Act: chamar appointmentService.schedule(...)
        var result = appointmentService.schedule(
                customer.getId(),
                professional.getId(),
                serviceOffering.getId(),
                scheduledAt, notes
        );
        // 8. Assert: validar status SCHEDULED, data, notas e relacionamentos
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledAt, result.getScheduledAt());
        assertEquals(notes, result.getNotes());
        assertEquals(customer, result.getCustomer());
        assertEquals(professional, result.getProfessional());
        assertEquals(serviceOffering, result.getServiceOffering());

        // 9. Verify: verificar chamadas aos repositories
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com customerId nulo")
    void shouldThrowExceptionWhenSchedulingWithNullCustomerId() {
        Long customerId = null;
        Long professionalId = 1L;
        Long serviceOfferingId = 2L;
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";
        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customerId,
                        professionalId, serviceOfferingId, scheduledAt, notes )
        );
        verifyNoInteractions(
                customerRepository,
                professionalRepository,
                serviceOfferingRepository,
                appointmentRepository
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com professionalId nulo")
    void shouldThrowExceptionWhenSchedulingWithNullProfessionalId() {
        Long customerId = 1L;
        Long professionalId = null;
        Long serviceOfferingId = 2L;
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";
        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customerId,
                        professionalId, serviceOfferingId, scheduledAt, notes )
        );
        verifyNoInteractions(
                customerRepository,
                professionalRepository,
                serviceOfferingRepository,
                appointmentRepository
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com serviceOfferingId nulo")
    void shouldThrowExceptionWhenSchedulingWithNullServiceOfferingId() {
        Long customerId = 1L;
        Long professionalId = 2L;
        Long serviceOfferingId = null;
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";
        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customerId,
                        professionalId, serviceOfferingId, scheduledAt, notes )
        );
        verifyNoInteractions(
                customerRepository,
                professionalRepository,
                serviceOfferingRepository,
                appointmentRepository
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar para cliente inexistente")
    void shouldThrowExceptionWhenSchedulingWithCustomerThatDoesNotExist() {
        Long customerId = 0L;
        Long professionalId = 1L;
        Long serviceOfferingId = 2L;
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.schedule(customerId,
                        professionalId, serviceOfferingId, scheduledAt, notes )
        );
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customerId);
        verifyNoInteractions(
                professionalRepository,
                serviceOfferingRepository,
                appointmentRepository
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com profissional inexistente")
    void shouldThrowExceptionWhenSchedulingWithProfessionalThatDoesNotExist() {
        Long customerId = 1L;
        Long professionalId = 99L;
        Long serviceOfferingId = 2L;
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";
        var customer = createValidCustomer();
        customer.setId(customerId);

        Mockito.when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));
        Mockito.when(professionalRepository.findById(professionalId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.schedule(customerId,
                        professionalId, serviceOfferingId, scheduledAt, notes)
        );

        Mockito.verify(customerRepository, Mockito.times(1)).findById(customerId);
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professionalId);
        verifyNoInteractions(
                serviceOfferingRepository,
                appointmentRepository
        );
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com servico inexistente")
    void shouldThrowExceptionWhenSchedulingWithServiceOfferingThatDoesNotExist() {
        Long customerId = 1L;
        Long professionalId = 2L;
        Long serviceOfferingId = 99L;
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";
        var customer = createValidCustomer();
        customer.setId(customerId);
        var professional = createValidProfessional();
        professional.setId(professionalId);

        Mockito.when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));
        Mockito.when(professionalRepository.findById(professionalId))
                .thenReturn(Optional.of(professional));
        Mockito.when(serviceOfferingRepository.findById(serviceOfferingId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.schedule(customerId,
                        professionalId, serviceOfferingId, scheduledAt, notes)
        );

        Mockito.verify(customerRepository, Mockito.times(1)).findById(customerId);
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professionalId);
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOfferingId);
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar para cliente inativo")
    void shouldThrowExceptionWhenSchedulingWithInactiveCustomer() {
        // 1. Arrange: criar Customer inativo, Professional ativo e ServiceOffering ativo
        var inactiveCustomer = createValidCustomer();
        inactiveCustomer.setId(1L);
        inactiveCustomer.deactivate();
        var professional = createValidProfessional();
        professional.setId(2L);
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(3L);
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";
        // 2. Arrange: simular repositories retornando as entidades
        Mockito.when(customerRepository
                .findById(inactiveCustomer.getId()))
                .thenReturn(Optional.of(inactiveCustomer));

        Mockito.when(professionalRepository
                        .findById(professional.getId()))
                .thenReturn(Optional.of(professional));

        Mockito.when(serviceOfferingRepository
                .findById(serviceOffering.getId()))
                .thenReturn(Optional.of(serviceOffering));
        // 3. Act & Assert: chamar appointmentService.schedule(...) e esperar DomainException
        assertThrows(DomainException.class,
                () -> appointmentService.schedule(inactiveCustomer.getId(),
                        professional.getId(), serviceOffering.getId(), scheduledAt, notes)
        );
        // 4. Verify: verificar que appointmentRepository.save nunca foi chamado
        Mockito.verify(customerRepository, Mockito.times(1)).findById(inactiveCustomer.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com profissional inativo")
    void shouldThrowExceptionWhenSchedulingWithInactiveProfessional() {
        var customer = createValidCustomer();
        customer.setId(1L);
        var inactiveProfessional = createValidProfessional();
        inactiveProfessional.setId(2L);
        inactiveProfessional.deactivate();
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(3L);
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";

        Mockito.when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        Mockito.when(professionalRepository.findById(inactiveProfessional.getId()))
                .thenReturn(Optional.of(inactiveProfessional));
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId()))
                .thenReturn(Optional.of(serviceOffering));

        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customer.getId(),
                        inactiveProfessional.getId(), serviceOffering.getId(), scheduledAt, notes)
        );

        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(inactiveProfessional.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar servico inativo")
    void shouldThrowExceptionWhenSchedulingWithInactiveServiceOffering() {
        var customer = createValidCustomer();
        customer.setId(1L);
        var professional = createValidProfessional();
        professional.setId(2L);
        var inactiveServiceOffering = createValidServiceOffering();
        inactiveServiceOffering.setId(3L);
        inactiveServiceOffering.deactivate();
        var scheduledAt = LocalDateTime.now().plusDays(1);
        var notes = "Primeiro atendimento";

        Mockito.when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        Mockito.when(professionalRepository.findById(professional.getId()))
                .thenReturn(Optional.of(professional));
        Mockito.when(serviceOfferingRepository.findById(inactiveServiceOffering.getId()))
                .thenReturn(Optional.of(inactiveServiceOffering));

        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customer.getId(),
                        professional.getId(), inactiveServiceOffering.getId(), scheduledAt, notes)
        );

        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(inactiveServiceOffering.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com data nula")
    void shouldThrowExceptionWhenSchedulingWithNullDate() {
        var customer = createValidCustomer();
        customer.setId(1L);
        var professional = createValidProfessional();
        professional.setId(2L);
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(3L);
        LocalDateTime nullScheduledAt = null;
        var notes = "Primeiro atendimento";

        Mockito.when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        Mockito.when(professionalRepository.findById(professional.getId()))
                .thenReturn(Optional.of(professional));
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId()))
                .thenReturn(Optional.of(serviceOffering));

        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customer.getId(),
                        professional.getId(), serviceOffering.getId(), nullScheduledAt, notes)
        );

        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao agendar com data no passado")
    void shouldThrowExceptionWhenSchedulingWithPastDate() {
        var customer = createValidCustomer();
        customer.setId(1L);
        var professional = createValidProfessional();
        professional.setId(2L);
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(3L);
        LocalDateTime pastScheduledAt = LocalDateTime.now().minusDays(4);
        var notes = "Primeiro atendimento";

        Mockito.when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        Mockito.when(professionalRepository.findById(professional.getId()))
                .thenReturn(Optional.of(professional));
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId()))
                .thenReturn(Optional.of(serviceOffering));

        assertThrows(DomainException.class,
                () -> appointmentService.schedule(customer.getId(),
                        professional.getId(), serviceOffering.getId(), pastScheduledAt, notes)
        );

        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    // =========================
    // Find appointments
    // =========================

    @Test
    @DisplayName("Deve buscar agendamento por id")
    void shouldFindAppointmentById() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        var result = appointmentService.findById(appointment.getId());

        assertEquals(appointment.getId(), result.getId());
        assertEquals(appointment.getStatus(), result.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar agendamento com id nulo")
    void shouldThrowExceptionWhenFindingAppointmentWithNullId() {
        assertThrows(DomainException.class, () -> appointmentService.findById(null));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar agendamento inexistente")
    void shouldThrowExceptionWhenFindingAppointmentThatDoesNotExist() {
        Long appointmentId = 99L;

        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.findById(appointmentId));

        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointmentId);
    }

    @Test
    @DisplayName("Deve listar todos os agendamentos")
    void shouldFindAllAppointments() {
        var firstAppointment = createValidScheduledAppointment();
        firstAppointment.setId(1L);
        var secondAppointment = createValidScheduledAppointment();
        secondAppointment.setId(2L);
        secondAppointment.setStatus(AppointmentStatus.CONFIRMED);
        var appointments = List.of(firstAppointment, secondAppointment);

        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(appointmentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(appointments, pageable, appointments.size()));

        var result = appointmentService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(firstAppointment.getId(), result.getContent().get(0).getId());
        assertEquals(secondAppointment.getId(), result.getContent().get(1).getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findAll(pageable);
    }

    // =========================
    // Confirm appointment
    // =========================

    @Test
    @DisplayName("Deve confirmar agendamento quando status for SCHEDULED")
    void shouldConfirmScheduledAppointment() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepository.save(appointment)).thenReturn(appointment);

        var result = appointmentService.confirm(appointment.getId());

        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);
    }

    @Test
    @DisplayName("Deve lancar excecao ao confirmar com id nulo")
    void shouldThrowExceptionWhenConfirmingWithNullId() {
        assertThrows(DomainException.class, () -> appointmentService.confirm(null));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao confirmar agendamento inexistente")
    void shouldThrowExceptionWhenConfirmingAppointmentThatDoesNotExist() {
        Long appointmentId = 99L;

        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.confirm(appointmentId));

        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointmentId);
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao confirmar agendamento com status invalido")
    void shouldThrowExceptionWhenConfirmingAppointmentWithInvalidStatus() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CANCELED);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.confirm(appointment.getId()));

        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    // =========================
    // Cancel appointment
    // =========================

    @Test
    @DisplayName("Deve cancelar agendamento sem motivo")
    void shouldCancelAppointmentWithoutReason() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepository.save(appointment)).thenReturn(appointment);

        var result = appointmentService.cancel(appointment.getId());

        assertEquals(AppointmentStatus.CANCELED, result.getStatus());
        assertEquals("", result.getCancelReason());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);
    }

    @Test
    @DisplayName("Deve cancelar agendamento com motivo")
    void shouldCancelAppointmentWithReason() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        var reason = "Cliente solicitou cancelamento";

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepository.save(appointment)).thenReturn(appointment);

        var result = appointmentService.cancel(appointment.getId(), reason);

        assertEquals(AppointmentStatus.CANCELED, result.getStatus());
        assertEquals(reason, result.getCancelReason());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar com id nulo")
    void shouldThrowExceptionWhenCancelingWithNullId() {
        assertThrows(DomainException.class, () -> appointmentService.cancel(null));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar agendamento inexistente")
    void shouldThrowExceptionWhenCancelingAppointmentThatDoesNotExist() {
        Long appointmentId = 99L;

        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.cancel(appointmentId));

        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointmentId);
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar com motivo nulo")
    void shouldThrowExceptionWhenCancelingWithNullReason() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.cancel(appointment.getId(), null));

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar agendamento com status invalido")
    void shouldThrowExceptionWhenCancelingAppointmentWithInvalidStatus() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.DONE);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.cancel(appointment.getId()));

        assertEquals(AppointmentStatus.DONE, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    // =========================
    // Mark appointment as done
    // =========================

    @Test
    @DisplayName("Deve concluir agendamento confirmado e com horario passado")
    void shouldMarkConfirmedAppointmentAsDone() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setScheduledAt(LocalDateTime.now().minusHours(1));

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepository.save(appointment)).thenReturn(appointment);

        var result = appointmentService.markAsDone(appointment.getId());

        assertEquals(AppointmentStatus.DONE, result.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);
    }

    @Test
    @DisplayName("Deve lancar excecao ao concluir com id nulo")
    void shouldThrowExceptionWhenMarkingAsDoneWithNullId() {
        assertThrows(DomainException.class, () -> appointmentService.markAsDone(null));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao concluir agendamento inexistente")
    void shouldThrowExceptionWhenMarkingAsDoneAppointmentThatDoesNotExist() {
        Long appointmentId = 99L;

        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.markAsDone(appointmentId));

        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointmentId);
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao concluir agendamento com status invalido")
    void shouldThrowExceptionWhenMarkingAsDoneWithInvalidStatus() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setScheduledAt(LocalDateTime.now().minusHours(1));

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.markAsDone(appointment.getId()));

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao concluir agendamento antes do horario")
    void shouldThrowExceptionWhenMarkingAsDoneBeforeScheduledTime() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.markAsDone(appointment.getId()));

        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    // =========================
    // Mark appointment as no-show
    // =========================

    @Test
    @DisplayName("Deve marcar falta quando agendamento estiver confirmado e fora da tolerancia")
    void shouldMarkConfirmedAppointmentAsNoShow() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setScheduledAt(LocalDateTime.now().minusMinutes(20));

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepository.save(appointment)).thenReturn(appointment);

        var result = appointmentService.markAsNoShow(appointment.getId());

        assertEquals(AppointmentStatus.NO_SHOW, result.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar falta com id nulo")
    void shouldThrowExceptionWhenMarkingNoShowWithNullId() {
        assertThrows(DomainException.class, () -> appointmentService.markAsNoShow(null));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar falta em agendamento inexistente")
    void shouldThrowExceptionWhenMarkingNoShowAppointmentThatDoesNotExist() {
        Long appointmentId = 99L;

        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.markAsNoShow(appointmentId));

        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointmentId);
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar falta com status invalido")
    void shouldThrowExceptionWhenMarkingNoShowWithInvalidStatus() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setScheduledAt(LocalDateTime.now().minusMinutes(30));

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.markAsNoShow(appointment.getId()));

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao marcar falta dentro da janela de tolerancia")
    void shouldThrowExceptionWhenMarkingNoShowWithinToleranceWindow() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setScheduledAt(LocalDateTime.now().minusMinutes(5));

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.markAsNoShow(appointment.getId()));

        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    // =========================
    // Reschedule appointment
    // =========================

    @Test
    @DisplayName("Deve reagendar agendamento quando status permitir e nova data for valida")
    void shouldRescheduleAppointmentWhenDataIsValid() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        var newScheduledAt = LocalDateTime.now().plusDays(3);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepository.save(appointment)).thenReturn(appointment);

        var result = appointmentService.reschedule(appointment.getId(), newScheduledAt);

        assertEquals(newScheduledAt, result.getScheduledAt());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(appointment);
    }

    @Test
    @DisplayName("Deve lancar excecao ao reagendar com id nulo")
    void shouldThrowExceptionWhenReschedulingWithNullId() {
        var newScheduledAt = LocalDateTime.now().plusDays(3);

        assertThrows(DomainException.class, () -> appointmentService.reschedule(null, newScheduledAt));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao reagendar agendamento inexistente")
    void shouldThrowExceptionWhenReschedulingAppointmentThatDoesNotExist() {
        Long appointmentId = 99L;
        var newScheduledAt = LocalDateTime.now().plusDays(3);

        Mockito.when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.reschedule(appointmentId, newScheduledAt)
        );

        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointmentId);
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao reagendar com data nula")
    void shouldThrowExceptionWhenReschedulingWithNullDate() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        var originalScheduledAt = appointment.getScheduledAt();

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.reschedule(appointment.getId(), null));

        assertEquals(originalScheduledAt, appointment.getScheduledAt());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao reagendar para data passada")
    void shouldThrowExceptionWhenReschedulingToPastDate() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        var originalScheduledAt = appointment.getScheduledAt();
        var pastDate = LocalDateTime.now().minusDays(1);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class, () -> appointmentService.reschedule(appointment.getId(), pastDate));

        assertEquals(originalScheduledAt, appointment.getScheduledAt());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao reagendar agendamento com status invalido")
    void shouldThrowExceptionWhenReschedulingAppointmentWithInvalidStatus() {
        var appointment = createValidScheduledAppointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.DONE);
        var originalScheduledAt = appointment.getScheduledAt();
        var newScheduledAt = LocalDateTime.now().plusDays(3);

        Mockito.when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(DomainException.class,
                () -> appointmentService.reschedule(appointment.getId(), newScheduledAt)
        );

        assertEquals(AppointmentStatus.DONE, appointment.getStatus());
        assertEquals(originalScheduledAt, appointment.getScheduledAt());
        Mockito.verify(appointmentRepository, Mockito.times(1)).findById(appointment.getId());
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any(Appointment.class));
    }
}
