package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.AppointmentCancelRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.AppointmentRescheduleRequestDTO;
import com.alberdev.study.appointmenthub.api.mappers.AppointmentMapper;
import com.alberdev.study.appointmenthub.application.services.AppointmentService;
import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.alberdev.study.appointmenthub.domain.entities.AppointmentTestData.createAppointmentRequestDTO;
import static com.alberdev.study.appointmenthub.domain.entities.AppointmentTestData.createValidScheduledAppointmentWithIds;
import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@Import(AppointmentMapper.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar um agendamento valido")
    void shouldCreateAValidAppointment() throws Exception {
        // 1. Arrange: criar requestDTO com os dados de envio
        var requestDTO = createAppointmentRequestDTO();

        // 2. Arrange: criar o Appointment que a service retornaria depois de salvar
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.setNotes(requestDTO.notes());

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // 3. Arrange: simular comportamento da service
        Mockito.when(service.schedule(
                Mockito.eq(requestDTO.customerId()),
                Mockito.eq(requestDTO.professionalId()),
                Mockito.eq(requestDTO.serviceOfferingId()),
                Mockito.eq(requestDTO.scheduledAt()),
                Mockito.eq(requestDTO.notes())
        )).thenReturn(appointment);

        // 4. Act & Assert: executar POST /api/v1/appointments e validar resposta
        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/appointments/1")))
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.customerId").value(requestDTO.customerId()))
                .andExpect(jsonPath("$.professionalId").value(requestDTO.professionalId()))
                .andExpect(jsonPath("$.serviceOfferingId").value(requestDTO.serviceOfferingId()))
                .andExpect(jsonPath("$.scheduledAt").value("2030-05-25T14:30:00"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.notes").value(requestDTO.notes()));

        // 5. Verify: verificar chamada da service com os dados do request
        Mockito.verify(service, Mockito.times(1)).schedule(
                Mockito.eq(requestDTO.customerId()),
                Mockito.eq(requestDTO.professionalId()),
                Mockito.eq(requestDTO.serviceOfferingId()),
                Mockito.eq(requestDTO.scheduledAt()),
                Mockito.eq(requestDTO.notes())
        );
    }

    @Test
    @DisplayName("Deve buscar agendamento por id")
    void shouldFindAppointmentById() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar Appointment valido com id e relacionamentos preenchidos
        var appointment = createValidScheduledAppointmentWithIds();
        // 3. Arrange: simular service.findById(id) retornando o Appointment
        Mockito.when(service.findById(id)).thenReturn(appointment);
        // 4. Act: executar GET /api/v1/appointments/{id}
        mockMvc.perform(get("/api/v1/appointments/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
        // 5. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 6. Assert: validar corpo JSON com id, customerId, professionalId, serviceOfferingId, scheduledAt e status
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.customerId").value(appointment.getCustomer().getId()))
                .andExpect(jsonPath("$.professionalId").value(appointment.getProfessional().getId()))
                .andExpect(jsonPath("$.serviceOfferingId").value(appointment.getServiceOffering().getId()))
                .andExpect(jsonPath("$.scheduledAt").value("2030-05-25T14:30:00"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.notes").value(appointment.getNotes()));
        // 7. Verify: verificar chamada ao service.findById(id)
        Mockito.verify(service, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve listar agendamentos de forma paginada")
    void shouldFindAppointmentsWithPagination() throws Exception {
        // 1. Arrange: criar Appointment valido com id e relacionamentos preenchidos
        var appointment = createValidScheduledAppointmentWithIds();

        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        // 2. Arrange: simular service.findAll(pageable) retornando PageImpl com o Appointment
        
        Mockito.when(service.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);
        // 3. Act: executar GET /api/v1/appointments
        mockMvc.perform(get("/api/v1/appointments")
                        .accept(MediaType.APPLICATION_JSON))
        // 4. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 5. Assert: validar que $.content e um array
                .andExpect(jsonPath("$.content").isArray())
        // 6. Assert: validar os campos do primeiro item da pagina
                .andExpect(jsonPath("$.content[0].id").value(appointment.getId()))
                .andExpect(jsonPath("$.content[0].customerId").value(appointment.getCustomer().getId()))
                .andExpect(jsonPath("$.content[0].professionalId").value(appointment.getProfessional().getId()))
                .andExpect(jsonPath("$.content[0].serviceOfferingId").value(appointment.getServiceOffering().getId()))
                .andExpect(jsonPath("$.content[0].scheduledAt").value("2030-05-25T14:30:00"))
                .andExpect(jsonPath("$.content[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$.content[0].notes").value(appointment.getNotes()));
        // 7. Verify: verificar chamada ao service.findAll(...)
        Mockito.verify(service, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve confirmar agendamento")
    void shouldConfirmAppointment() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar Appointment retornado pela service com status CONFIRMED
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.confirm();

        // 3. Arrange: simular service.confirm(id) retornando o Appointment confirmado
        Mockito.when(service.confirm(id)).thenReturn(appointment);
        // 4. Act: executar PATCH /api/v1/appointments/{id}/confirm
        mockMvc.perform(patch("/api/v1/appointments/{id}/confirm", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 5. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 6. Assert: validar que $.status e CONFIRMED
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.status").value(appointment.getStatus().name()));
        // 7. Verify: verificar chamada ao service.confirm(id)
        Mockito.verify(service, Mockito.times(1)).confirm(id);
    }

    @Test
    @DisplayName("Deve cancelar agendamento com motivo")
    void shouldCancelAppointmentWithReason() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar AppointmentCancelRequestDTO com reason
        var requestDTO = new AppointmentCancelRequestDTO("Cliente solicitou cancelamento");
        // 3. Arrange: serializar requestDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(requestDTO);
        // 4. Arrange: criar Appointment retornado pela service com status CANCELED e cancelReason preenchido
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.cancel(requestDTO.reason());
        // 5. Arrange: simular service.cancel(id, reason) retornando o Appointment cancelado
        Mockito.when(service.cancel(Mockito.eq(id), Mockito.eq(requestDTO.reason())))
                .thenReturn(appointment);
        // 6. Act: executar PATCH /api/v1/appointments/{id}/cancel com body
        mockMvc.perform(patch("/api/v1/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
        // 7. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 8. Assert: validar $.status e $.cancelReason
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.cancelReason").value(requestDTO.reason()));
        // 9. Verify: verificar chamada ao service.cancel(id, reason)
        Mockito.verify(service, Mockito.times(1)).cancel(id, requestDTO.reason());
    }

    @Test
    @DisplayName("Deve cancelar agendamento sem motivo")
    void shouldCancelAppointmentWithoutReason() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar Appointment retornado pela service com status CANCELED
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.cancel();
        // 3. Arrange: simular service.cancel(id) retornando o Appointment cancelado
        Mockito.when(service.cancel(id)).thenReturn(appointment);
        // 4. Act: executar PATCH /api/v1/appointments/{id}/cancel sem body
        mockMvc.perform(patch("/api/v1/appointments/{id}/cancel", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 5. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 6. Assert: validar $.status como CANCELED
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.status").value("CANCELED"));
        // 7. Verify: verificar chamada ao service.cancel(id)
        Mockito.verify(service, Mockito.times(1)).cancel(id);
        // 8. Verify: verificar que service.cancel(id, reason) nao foi chamado
        Mockito.verify(service, Mockito.never()).cancel(Mockito.eq(id), Mockito.anyString());
    }

    @Test
    @DisplayName("Deve reagendar agendamento")
    void shouldRescheduleAppointment() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;

        // 2. Arrange: criar AppointmentRescheduleRequestDTO com nova data futura
        var requestDTO = new AppointmentRescheduleRequestDTO(
                LocalDateTime.of(2030, 5, 26, 15, 0)
        );

        // 3. Arrange: serializar requestDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // 4. Arrange: criar Appointment retornado pela service com scheduledAt atualizado e status SCHEDULED
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.setId(id);
        appointment.setScheduledAt(requestDTO.scheduledAt());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // 5. Arrange: simular service.reschedule(id, scheduledAt) retornando o Appointment reagendado
        Mockito.when(service.reschedule(
                Mockito.eq(id),
                Mockito.eq(requestDTO.scheduledAt())
        )).thenReturn(appointment);

        // 6. Act & Assert: executar PATCH /api/v1/appointments/{id}/reschedule com body
        mockMvc.perform(patch("/api/v1/appointments/{id}/reschedule", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))

                // 7. Assert: validar status 200 OK
                .andExpect(status().isOk())

                // 8. Assert: validar $.scheduledAt e $.status
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.scheduledAt").value("2030-05-26T15:00:00"))
                .andExpect(jsonPath("$.status").value(appointment.getStatus().name()));

        // 9. Verify: verificar chamada ao service.reschedule(id, scheduledAt)
        Mockito.verify(service, Mockito.times(1)).reschedule(
                Mockito.eq(id),
                Mockito.eq(requestDTO.scheduledAt())
        );
    }

    @Test
    @DisplayName("Deve marcar agendamento como concluido")
    void shouldMarkAppointmentAsDone() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar Appointment retornado pela service com status DONE
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.setStatus(AppointmentStatus.DONE);
        // 3. Arrange: simular service.markAsDone(id) retornando o Appointment concluido
        Mockito.when(service.markAsDone(id)).thenReturn(appointment);
        // 4. Act: executar PATCH /api/v1/appointments/{id}/done
        mockMvc.perform(patch("/api/v1/appointments/{id}/done", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 5. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 6. Assert: validar $.status como DONE
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.status").value("DONE"));
        // 7. Verify: verificar chamada ao service.markAsDone(id)
        Mockito.verify(service, Mockito.times(1)).markAsDone(id);
    }

    @Test
    @DisplayName("Deve marcar agendamento como falta")
    void shouldMarkAppointmentAsNoShow() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar Appointment retornado pela service com status NO_SHOW
        var appointment = createValidScheduledAppointmentWithIds();
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        // 3. Arrange: simular service.markAsNoShow(id) retornando o Appointment com falta
        Mockito.when(service.markAsNoShow(id)).thenReturn(appointment);
        // 4. Act: executar PATCH /api/v1/appointments/{id}/no-show
        mockMvc.perform(patch("/api/v1/appointments/{id}/no-show", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 5. Assert: validar status 200 OK
                .andExpect(status().isOk())
        // 6. Assert: validar $.status como NO_SHOW
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
        // 7. Verify: verificar chamada ao service.markAsNoShow(id)
        Mockito.verify(service, Mockito.times(1)).markAsNoShow(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao buscar agendamento inexistente")
    void shouldReturnErrorWhenAppointmentDoesNotExist() throws Exception {
        // 1. Arrange: definir id inexistente
        Long id = 99L;
        // 2. Arrange: simular service.findById(id) lancando ResourceNotFoundException
        Mockito.when(service.findById(Mockito.eq(id))).thenThrow(
                new ResourceNotFoundException("Agendamento nao encontrado com id " + id)
        );
        // 3. Act: executar GET /api/v1/appointments/{id}
        mockMvc.perform(get("/api/v1/appointments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 4. Assert: validar status 404 Not Found
                .andExpect(status().isNotFound())
        // 5. Assert: validar corpo do erro com status, error, message e path

                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Agendamento nao encontrado com id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments/" + id));

        // 6. Verify: verificar chamada ao service.findById(id)
        Mockito.verify(service, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao confirmar agendamento com status invalido")
    void shouldReturnErrorWhenConfirmingAppointmentWithInvalidStatus() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: simular service.confirm(id) lancando DomainException
        Mockito.when(service.confirm(id))
                .thenThrow(new DomainException("So e possivel confirmar agendamentos pendentes."));
        // 3. Act: executar PATCH /api/v1/appointments/{id}/confirm
        mockMvc.perform(patch("/api/v1/appointments/{id}/confirm", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 4. Assert: validar status 400 Bad Request
                .andExpect(status().isBadRequest())
        // 5. Assert: validar corpo do erro com status, error, message e path
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("So e possivel confirmar agendamentos pendentes."))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments/" + id + "/confirm"));
        // 6. Verify: verificar chamada ao service.confirm(id)
        Mockito.verify(service, Mockito.times(1)).confirm(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao criar agendamento com regra invalida")
    void shouldReturnErrorWhenSchedulingWithInvalidRule() throws Exception {
        var requestDTO = createAppointmentRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        Mockito.when(service.schedule(
                Mockito.eq(requestDTO.customerId()),
                Mockito.eq(requestDTO.professionalId()),
                Mockito.eq(requestDTO.serviceOfferingId()),
                Mockito.eq(requestDTO.scheduledAt()),
                Mockito.eq(requestDTO.notes())
        )).thenThrow(new DomainException("Nao e possivel agendar para um cliente inativo."));

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Nao e possivel agendar para um cliente inativo."))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments"));

        Mockito.verify(service, Mockito.times(1)).schedule(
                Mockito.eq(requestDTO.customerId()),
                Mockito.eq(requestDTO.professionalId()),
                Mockito.eq(requestDTO.serviceOfferingId()),
                Mockito.eq(requestDTO.scheduledAt()),
                Mockito.eq(requestDTO.notes())
        );
    }

    @Test
    @DisplayName("Deve lancar erro ao cancelar agendamento com status invalido")
    void shouldReturnErrorWhenCancelingAppointmentWithInvalidStatus() throws Exception {
        Long id = 1L;
        var requestDTO = new AppointmentCancelRequestDTO("Cliente solicitou cancelamento");
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        Mockito.when(service.cancel(Mockito.eq(id), Mockito.eq(requestDTO.reason())))
                .thenThrow(new DomainException("Operacao nao permitida para o status atual."));

        mockMvc.perform(patch("/api/v1/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Operacao nao permitida para o status atual."))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments/" + id + "/cancel"));

        Mockito.verify(service, Mockito.times(1)).cancel(id, requestDTO.reason());
    }

    @Test
    @DisplayName("Deve lancar erro ao reagendar para data invalida")
    void shouldReturnErrorWhenReschedulingToInvalidDate() throws Exception {
        Long id = 1L;
        var requestDTO = new AppointmentRescheduleRequestDTO(
                LocalDateTime.of(2020, 1, 1, 10, 0)
        );
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(patch("/api/v1/appointments/{id}/reschedule", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("A data do agendamento nao pode estar no passado."))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments/" + id + "/reschedule"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("scheduledAt"))
                .andExpect(jsonPath("$.errors[0].message").value("A data do agendamento nao pode estar no passado."));

        Mockito.verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve lancar erro ao concluir agendamento com status invalido")
    void shouldReturnErrorWhenMarkingAppointmentAsDoneWithInvalidStatus() throws Exception {
        Long id = 1L;

        Mockito.when(service.markAsDone(id))
                .thenThrow(new DomainException("Operacao nao permitida para o status atual."));

        mockMvc.perform(patch("/api/v1/appointments/{id}/done", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Operacao nao permitida para o status atual."))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments/" + id + "/done"));

        Mockito.verify(service, Mockito.times(1)).markAsDone(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao marcar falta com status invalido")
    void shouldReturnErrorWhenMarkingAppointmentAsNoShowWithInvalidStatus() throws Exception {
        Long id = 1L;

        Mockito.when(service.markAsNoShow(id))
                .thenThrow(new DomainException("Operacao nao permitida para o status atual."));

        mockMvc.perform(patch("/api/v1/appointments/{id}/no-show", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Operacao nao permitida para o status atual."))
                .andExpect(jsonPath("$.path").value("/api/v1/appointments/" + id + "/no-show"));

        Mockito.verify(service, Mockito.times(1)).markAsNoShow(id);
    }
}
