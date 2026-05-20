package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.mappers.ServiceOfferingMapper;
import com.alberdev.study.appointmenthub.application.services.ServiceOfferingService;
import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
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

import java.math.BigDecimal;
import java.util.List;

import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createServiceOfferingRequestDTO;
import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createValidServiceOffering;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceOfferingController.class)
@Import(ServiceOfferingMapper.class)
class ServiceOfferingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServiceOfferingService service;

    @Test
    @DisplayName("Deve criar um novo servico valido")
    void shouldCreateAValidNewServiceOffering() throws Exception {
        var requestDTO = createServiceOfferingRequestDTO();
        var savedServiceOffering = createValidServiceOffering();
        savedServiceOffering.setId(1L);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        Mockito.when(service.create(Mockito.any(ServiceOffering.class))).thenReturn(savedServiceOffering);

        mockMvc.perform(post("/api/v1/service-offerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/api/v1/service-offerings/1")))
                .andExpect(jsonPath("$.id").value(savedServiceOffering.getId()))
                .andExpect(jsonPath("$.name").value(savedServiceOffering.getName()));

        Mockito.verify(service, Mockito.times(1))
                .create(org.mockito.ArgumentMatchers.any(ServiceOffering.class));
    }

    @Test
    @DisplayName("Deve buscar um servico por id")
    void shouldFindAServiceOfferingById() throws Exception {
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);

        Mockito.when(service.findById(serviceOffering.getId())).thenReturn(serviceOffering);

        mockMvc.perform(get("/api/v1/service-offerings/{id}", serviceOffering.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(serviceOffering.getId()))
                .andExpect(jsonPath("$.name").value(serviceOffering.getName()))
                .andExpect(jsonPath("$.durationInMinutes").value(serviceOffering.getDurationInMinutes()))
                .andExpect(jsonPath("$.basePrice").value(serviceOffering.getBasePrice().doubleValue()))
                .andExpect(jsonPath("$.active").value(serviceOffering.isActive()));

        Mockito.verify(service, Mockito.times(1)).findById(serviceOffering.getId());
    }

    @Test
    @DisplayName("Deve buscar servicos de forma paginada")
    void shouldFindServiceOfferingsWithPagination() throws Exception {
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);

        Mockito.when(service.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(serviceOffering)));

        mockMvc.perform(get("/api/v1/service-offerings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(serviceOffering.getId()))
                .andExpect(jsonPath("$.content[0].name").value(serviceOffering.getName()))
                .andExpect(jsonPath("$.content[0].durationInMinutes").value(serviceOffering.getDurationInMinutes()))
                .andExpect(jsonPath("$.content[0].basePrice").value(serviceOffering.getBasePrice().doubleValue()))
                .andExpect(jsonPath("$.content[0].active").value(serviceOffering.isActive()));

        Mockito.verify(service, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar servico quando dados forem validos")
    void shouldUpdateServiceOfferingWhenDataIsValid() throws Exception {
        Long id = 1L;
        var requestDTO = createServiceOfferingRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        var updatedServiceOffering = createValidServiceOffering();
        updatedServiceOffering.setId(id);

        Mockito.when(service.updateServiceOffering(Mockito.eq(id), Mockito.any(ServiceOffering.class)))
                .thenReturn(updatedServiceOffering);

        mockMvc.perform(patch("/api/v1/service-offerings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedServiceOffering.getId()))
                .andExpect(jsonPath("$.name").value(updatedServiceOffering.getName()))
                .andExpect(jsonPath("$.durationInMinutes").value(updatedServiceOffering.getDurationInMinutes()))
                .andExpect(jsonPath("$.basePrice").value(updatedServiceOffering.getBasePrice().doubleValue()))
                .andExpect(jsonPath("$.active").value(updatedServiceOffering.isActive()));

        Mockito.verify(service, Mockito.times(1))
                .updateServiceOffering(Mockito.eq(id), Mockito.any(ServiceOffering.class));
    }

    @Test
    @DisplayName("Deve ativar servico")
    void shouldActivateServiceOffering() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(service).activate(id);

        mockMvc.perform(patch("/api/v1/service-offerings/{id}/activate", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).activate(id);
    }

    @Test
    @DisplayName("Deve desativar servico")
    void shouldDeactivateServiceOffering() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(service).deactivate(id);

        mockMvc.perform(patch("/api/v1/service-offerings/{id}/deactivate", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).deactivate(id);
    }

    @Test
    @DisplayName("Deve atualizar preco base do servico")
    void shouldUpdateServiceOfferingBasePrice() throws Exception {
        Long id = 1L;
        var requestDTO = createServiceOfferingRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        var updatedServiceOffering = createValidServiceOffering();
        updatedServiceOffering.setId(id);
        updatedServiceOffering.setBasePrice(requestDTO.basePrice());

        Mockito.when(service.updateBasePrice(Mockito.eq(id), Mockito.any(BigDecimal.class)))
                .thenReturn(updatedServiceOffering);

        mockMvc.perform(patch("/api/v1/service-offerings/{id}/base-price", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.basePrice").value(requestDTO.basePrice().doubleValue()));

        Mockito.verify(service, Mockito.times(1))
                .updateBasePrice(Mockito.eq(id), Mockito.any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deve atualizar duracao do servico")
    void shouldUpdateServiceOfferingDuration() throws Exception {
        Long id = 1L;
        var requestDTO = createServiceOfferingRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        var updatedServiceOffering = createValidServiceOffering();
        updatedServiceOffering.setId(id);
        updatedServiceOffering.setDurationInMinutes(requestDTO.durationInMinutes());

        Mockito.when(service.updateDuration(Mockito.eq(id), Mockito.any(Integer.class)))
                .thenReturn(updatedServiceOffering);

        mockMvc.perform(patch("/api/v1/service-offerings/{id}/duration", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.durationInMinutes").value(requestDTO.durationInMinutes()));

        Mockito.verify(service, Mockito.times(1))
                .updateDuration(Mockito.eq(id), Mockito.any(Integer.class));
    }

}
