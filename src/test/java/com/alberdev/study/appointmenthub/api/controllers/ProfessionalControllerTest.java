package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.ProfessionalRequestDTO;
import com.alberdev.study.appointmenthub.api.mappers.ProfessionalMapper;
import com.alberdev.study.appointmenthub.application.services.ProfessionalService;
import com.alberdev.study.appointmenthub.domain.entities.Professional;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createProfessionalRequestDTO;
import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createValidProfessional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessionalController.class)
@Import(ProfessionalMapper.class)
class ProfessionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfessionalService service;

    @Test
    @DisplayName("Deve criar um novo profissional valido")
    void shouldCreateAValidNewProfessional() throws Exception {
        var requestDTO = createProfessionalRequestDTO();
        var savedProfessional = createValidProfessional();
        savedProfessional.setId(1L);
        savedProfessional.getAppUser().setId(requestDTO.appUserId());

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        Mockito.when(service.create(Mockito.any(Professional.class))).thenReturn(savedProfessional);

        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/api/v1/professionals/1")))
                .andExpect(jsonPath("$.id").value(savedProfessional.getId()))
                .andExpect(jsonPath("$.name").value(savedProfessional.getName()))
                .andExpect(jsonPath("$.specialty").value(savedProfessional.getSpecialty()))
                .andExpect(jsonPath("$.active").value(savedProfessional.isActive()))
                .andExpect(jsonPath("$.appUserId").value(requestDTO.appUserId()));

        Mockito.verify(service, Mockito.times(1)).create(Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve buscar profissional por id")
    void shouldFindProfessionalById() throws Exception {
        var professional = createValidProfessional();
        professional.setId(1L);
        professional.getAppUser().setId(1L);

        Mockito.when(service.findById(professional.getId())).thenReturn(professional);

        mockMvc.perform(get("/api/v1/professionals/{id}", professional.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(professional.getId()))
                .andExpect(jsonPath("$.name").value(professional.getName()))
                .andExpect(jsonPath("$.specialty").value(professional.getSpecialty()))
                .andExpect(jsonPath("$.active").value(professional.isActive()))
                .andExpect(jsonPath("$.appUserId").value(professional.getAppUser().getId()));

        Mockito.verify(service, Mockito.times(1)).findById(professional.getId());
    }

    @Test
    @DisplayName("Deve buscar profissionais de forma paginada")
    void shouldFindProfessionalsWithPagination() throws Exception {
        var professional = createValidProfessional();
        professional.setId(1L);
        professional.getAppUser().setId(1L);

        Mockito.when(service.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(professional)));

        mockMvc.perform(get("/api/v1/professionals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(professional.getId()))
                .andExpect(jsonPath("$.content[0].name").value(professional.getName()))
                .andExpect(jsonPath("$.content[0].specialty").value(professional.getSpecialty()))
                .andExpect(jsonPath("$.content[0].active").value(professional.isActive()))
                .andExpect(jsonPath("$.content[0].appUserId").value(professional.getAppUser().getId()));

        Mockito.verify(service, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar profissional quando dados forem validos")
    void shouldUpdateProfessionalWhenDataIsValid() throws Exception {
        Long id = 1L;
        var requestDTO = createProfessionalRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        var updatedProfessional = createValidProfessional();
        updatedProfessional.setId(id);
        updatedProfessional.getAppUser().setId(requestDTO.appUserId());

        Mockito.when(service.update(Mockito.eq(id), Mockito.any(Professional.class)))
                .thenReturn(updatedProfessional);

        mockMvc.perform(patch("/api/v1/professionals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedProfessional.getId()))
                .andExpect(jsonPath("$.name").value(updatedProfessional.getName()))
                .andExpect(jsonPath("$.specialty").value(updatedProfessional.getSpecialty()))
                .andExpect(jsonPath("$.active").value(updatedProfessional.isActive()))
                .andExpect(jsonPath("$.appUserId").value(requestDTO.appUserId()));

        Mockito.verify(service, Mockito.times(1))
                .update(Mockito.eq(id), Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve ativar profissional")
    void shouldActivateProfessional() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(service).activate(id);

        mockMvc.perform(patch("/api/v1/professionals/{id}/activate", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).activate(id);
    }

    @Test
    @DisplayName("Deve desativar profissional")
    void shouldDeactivateProfessional() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(service).deactivate(id);

        mockMvc.perform(patch("/api/v1/professionals/{id}/deactivate", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).deactivate(id);
    }

    @Test
    @DisplayName("Deve atualizar especialidade do profissional")
    void shouldUpdateProfessionalSpecialty() throws Exception {
        Long id = 1L;
        var requestDTO = createProfessionalRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        var updatedProfessional = createValidProfessional();
        updatedProfessional.setId(id);
        updatedProfessional.setSpecialty(requestDTO.specialty());
        updatedProfessional.getAppUser().setId(requestDTO.appUserId());

        Mockito.when(service.updateSpecialty(Mockito.eq(id), Mockito.any(String.class)))
                .thenReturn(updatedProfessional);

        mockMvc.perform(patch("/api/v1/professionals/{id}/specialty", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.specialty").value(requestDTO.specialty()));

        Mockito.verify(service, Mockito.times(1))
                .updateSpecialty(Mockito.eq(id), Mockito.any(String.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao buscar profissional inexistente")
    void shouldReturnErrorWhenProfessionalDoesNotExist() throws Exception {
        // 1. Arrange: definir id inexistente
        long id = 99L;
        // 2. Arrange: simular service.findById(id) lancando ResourceNotFoundException
        Mockito.when(service.findById(id))
                .thenThrow(new ResourceNotFoundException("Profissional nao encontrado"));
        // 3. Act: executar GET /api/v1/professionals/{id}
        mockMvc.perform(get("/api/v1/professionals/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 4. Assert: validar status esperado para recurso nao encontrado
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profissional nao encontrado"))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals/" + id));
        // 5. Verify: verificar chamada ao service.findById(id)
        Mockito.verify(service, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao criar profissional sem usuario")
    void shouldReturnErrorWhenCreatingProfessionalWithoutAppUser() throws Exception {
        // 1. Arrange: criar requestDTO sem appUserId
        var requestDTO = new ProfessionalRequestDTO(
                "Dr Marcio",
                "Cardiologista",
                null
        );

        // 2. Arrange: serializar requestDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // 3. Act: executar POST /api/v1/professionals
        mockMvc.perform(post("/api/v1/professionals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))

                // 4. Assert: validar status esperado para erro de validacao
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("O profissional precisa estar vinculado a um usuario."))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals"));

        // 5. Verify: validar que a service nao foi chamada quando o DTO e invalido
        Mockito.verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve lancar erro ao criar profissional com usuario ja vinculado")
    void shouldReturnErrorWhenCreatingProfessionalWithAlreadyLinkedAppUser() throws Exception {
        // 1. Arrange: criar requestDTO valido
        var requestDTO = createProfessionalRequestDTO();
        // 2. Arrange: serializar requestDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(requestDTO);
        // 3. Arrange: simular service.create(...) lancando ResourceAlreadyExistsException
        Mockito.when(service.create(Mockito.any(Professional.class)))
                .thenThrow(new ResourceAlreadyExistsException("Ja existe um profissional cadastrado com esse usuario."));
        // 4. Act: executar POST /api/v1/professionals
        mockMvc.perform(post("/api/v1/professionals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
        // 5. Assert: validar status esperado para conflito/recurso ja existente
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Ja existe um profissional cadastrado com esse usuario."))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals"));
        // 6. Verify: verificar chamada ao service.create(...)
        Mockito.verify(service, Mockito.times(1)).create(Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao atualizar profissional inexistente")
    void shouldReturnErrorWhenUpdatingProfessionalThatDoesNotExist() throws Exception {
        // 1. Arrange: definir id inexistente
        Long id = 99L;
        // 2. Arrange: criar requestDTO valido
        var requestDTO = createProfessionalRequestDTO();
        // 3. Arrange: serializar requestDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(requestDTO);
        // 4. Arrange: simular service.update(id, ...) lancando ResourceNotFoundException
        Mockito.when(service.update(Mockito.eq(id), Mockito.any(Professional.class)))
                .thenThrow(new ResourceNotFoundException("Profissional nao encontrado com Id " + id));
        // 5. Act: executar PATCH /api/v1/professionals/{id}
        mockMvc.perform(patch("/api/v1/professionals/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
        // 6. Assert: validar status esperado para recurso nao encontrado
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profissional nao encontrado com Id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals/" + id));
        // 7. Verify: verificar chamada ao service.update(...)
        Mockito.verify(service, Mockito.times(1))
                .update(Mockito.eq(id), Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao ativar profissional inexistente")
    void shouldReturnErrorWhenActivatingProfessionalThatDoesNotExist() throws Exception {
        // 1. Arrange: definir id inexistente
        Long id = 99L;
        // 2. Arrange: simular service.activate(id) lancando ResourceNotFoundException
        Mockito.doThrow(new ResourceNotFoundException("Profissional nao encontrado com Id " + id))
                .when(service).activate(id);
        // 3. Act: executar PATCH /api/v1/professionals/{id}/activate
        mockMvc.perform(patch("/api/v1/professionals/{id}/activate", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 4. Assert: validar status esperado para recurso nao encontrado
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profissional nao encontrado com Id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals/" + id + "/activate"));
        // 5. Verify: verificar chamada ao service.activate(id)
        Mockito.verify(service, Mockito.times(1)).activate(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao desativar profissional inexistente")
    void shouldReturnErrorWhenDeactivatingProfessionalThatDoesNotExist() throws Exception {
        // 1. Arrange: definir id inexistente
        Long id = 99L;
        // 2. Arrange: simular service.deactivate(id) lancando ResourceNotFoundException
        Mockito.doThrow(new ResourceNotFoundException("Profissional nao encontrado com Id " + id))
                .when(service).deactivate(id);
        // 3. Act: executar PATCH /api/v1/professionals/{id}/deactivate
        mockMvc.perform(patch("/api/v1/professionals/{id}/deactivate", id)
                        .accept(MediaType.APPLICATION_JSON))
        // 4. Assert: validar status esperado para recurso nao encontrado
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profissional nao encontrado com Id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals/" + id + "/deactivate"));
        // 5. Verify: verificar chamada ao service.deactivate(id)
        Mockito.verify(service, Mockito.times(1)).deactivate(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao atualizar especialidade invalida")
    void shouldReturnErrorWhenUpdatingProfessionalWithInvalidSpecialty() throws Exception {
        // 1. Arrange: definir id existente
        Long id = 1L;
        // 2. Arrange: criar requestDTO com specialty nula ou em branco
        var requestDTO = new ProfessionalRequestDTO(
                "Dr Marcio",
                " ",
                1L
        );
        // 3. Arrange: serializar requestDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(requestDTO);
        // 4. Arrange: simular service.updateSpecialty(id, specialty) lancando DomainException
        Mockito.when(service.updateSpecialty(Mockito.eq(id), Mockito.eq(requestDTO.specialty())))
                .thenThrow(new DomainException("Especialidade nao pode ser nula ou em branco"));
        // 5. Act: executar PATCH /api/v1/professionals/{id}/specialty
        mockMvc.perform(patch("/api/v1/professionals/{id}/specialty", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
        // 6. Assert: validar status esperado para erro de regra de dominio
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Especialidade nao pode ser nula ou em branco"))
                .andExpect(jsonPath("$.path").value("/api/v1/professionals/" + id + "/specialty"));
        // 7. Verify: verificar chamada ao service.updateSpecialty(id, specialty)
        Mockito.verify(service, Mockito.times(1))
                .updateSpecialty(Mockito.eq(id), Mockito.eq(requestDTO.specialty()));
    }
}
