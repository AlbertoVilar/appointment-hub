package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.CustomerRequestDTO;
import com.alberdev.study.appointmenthub.api.mappers.CustomerMapper;
import com.alberdev.study.appointmenthub.application.services.CustomerService;
import com.alberdev.study.appointmenthub.domain.entities.Customer;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.alberdev.study.appointmenthub.domain.entities.CustomerTestData.createCustomerRequestDTO;
import static com.alberdev.study.appointmenthub.domain.entities.CustomerTestData.createValidCustomer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(CustomerMapper.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar um cliente quando o ID for valido")
    void shouldReturnCustomerWhenIdIsValid() throws Exception {
        var customer = createValidCustomer();
        customer.setId(1L);

        Mockito.when(customerService.findById(1L)).thenReturn(customer);

        mockMvc.perform(get("/api/v1/customers/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Joao Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.phone").value("11999999999"))
                .andExpect(jsonPath("$.active").value(true));

        Mockito.verify(customerService, Mockito.times(1)).findById(customer.getId());
    }

    @Test
    @DisplayName("Deve retornar uma lista de clientes quando houver dados")
    void shouldReturnCustomersWhenCustomersExist() throws Exception {
        var customer = createValidCustomer();
        customer.setId(1L);

        Mockito.when(customerService.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(customer)));

        mockMvc.perform(get("/api/v1/customers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Joao Silva"))
                .andExpect(jsonPath("$.content[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$.content[0].phone").value("11999999999"))
                .andExpect(jsonPath("$.content[0].active").value(true));

        Mockito.verify(customerService, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve criar cliente quando dados forem validos")
    void shouldCreateCustomerWhenDataIsValid() throws Exception {
        var requestDto = createCustomerRequestDTO();
        var savedCustomer = createValidCustomer();
        savedCustomer.setId(1L);

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        Mockito.when(customerService.create(org.mockito.ArgumentMatchers.any(Customer.class)))
                .thenReturn(savedCustomer);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/api/v1/customers/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(savedCustomer.getName()));

        Mockito.verify(customerService, Mockito.times(1))
                .create(org.mockito.ArgumentMatchers.any(Customer.class));
    }

    @Test
    @DisplayName("Deve retornar erros de validacao ao criar cliente com nome invalido")
    void shouldReturnValidationErrorsWhenCreatingCustomerWithInvalidName() throws Exception {
        var requestDto = new CustomerRequestDTO(
                "A",
                "cliente@email.com",
                "11999999999"
        );
        String jsonBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("O nome deve ter entre 3 e 50 caracteres"))
                .andExpect(jsonPath("$.path").value("/api/v1/customers"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("O nome deve ter entre 3 e 50 caracteres"));

        Mockito.verify(customerService, Mockito.never()).create(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve atualizar cliente quando dados forem validos")
    void shouldUpdateCustomerWhenDataIsValid() throws Exception {
        Long id = 1L;
        var requestValidDTO = createCustomerRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestValidDTO);

        var updatedCustomer = createValidCustomer();
        updatedCustomer.setId(id);
        updatedCustomer.setName("Nome Atualizado");

        Mockito.when(customerService.update(Mockito.eq(id), Mockito.any(Customer.class)))
                .thenReturn(updatedCustomer);

        mockMvc.perform(patch("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Nome Atualizado"))
                .andExpect(jsonPath("$.email").value(updatedCustomer.getEmail()));

        Mockito.verify(customerService, Mockito.times(1))
                .update(Mockito.eq(id), Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve ativar cliente")
    void shouldActivateCustomer() throws Exception {
        Long id = 1L;

        mockMvc.perform(patch("/api/v1/customers/{id}/activate", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(customerService, Mockito.times(1)).activate(id);
    }

    @Test
    @DisplayName("Deve desativar cliente")
    void shouldDeactivateCustomer() throws Exception {
        Long id = 1L;

        mockMvc.perform(patch("/api/v1/customers/{id}/deactivate", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(customerService, Mockito.times(1)).deactivate(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao buscar cliente inexistente")
    void shouldReturnErrorWhenCustomerDoesNotExist() throws Exception {
        Long id = 99L;

        Mockito.when(customerService.findById(id))
                .thenThrow(new ResourceNotFoundException("Cliente nao encontrado com id " + id));

        mockMvc.perform(get("/api/v1/customers/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado com id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/customers/" + id));

        Mockito.verify(customerService, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao criar cliente com email ja cadastrado")
    void shouldReturnErrorWhenCreatingCustomerWithExistingEmail() throws Exception {
        var requestDto = createCustomerRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDto);

        Mockito.when(customerService.create(Mockito.any(Customer.class)))
                .thenThrow(new ResourceAlreadyExistsException("Ja existe cliente cadastrado com este email."));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Ja existe cliente cadastrado com este email."))
                .andExpect(jsonPath("$.path").value("/api/v1/customers"));

        Mockito.verify(customerService, Mockito.times(1)).create(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao atualizar cliente inexistente")
    void shouldReturnErrorWhenUpdatingCustomerThatDoesNotExist() throws Exception {
        Long id = 99L;
        var requestDto = createCustomerRequestDTO();
        String jsonBody = objectMapper.writeValueAsString(requestDto);

        Mockito.when(customerService.update(Mockito.eq(id), Mockito.any(Customer.class)))
                .thenThrow(new ResourceNotFoundException("Cliente nao encontrado com id " + id));

        mockMvc.perform(patch("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado com id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/customers/" + id));

        Mockito.verify(customerService, Mockito.times(1))
                .update(Mockito.eq(id), Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao ativar cliente ja ativo")
    void shouldReturnErrorWhenActivatingAlreadyActiveCustomer() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new DomainException("Cliente ja esta ativo."))
                .when(customerService).activate(id);

        mockMvc.perform(patch("/api/v1/customers/{id}/activate", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Cliente ja esta ativo."))
                .andExpect(jsonPath("$.path").value("/api/v1/customers/" + id + "/activate"));

        Mockito.verify(customerService, Mockito.times(1)).activate(id);
    }

    @Test
    @DisplayName("Deve lancar erro ao desativar cliente inexistente")
    void shouldReturnErrorWhenDeactivatingCustomerThatDoesNotExist() throws Exception {
        Long id = 99L;

        Mockito.doThrow(new ResourceNotFoundException("Cliente nao encontrado com id " + id))
                .when(customerService).deactivate(id);

        mockMvc.perform(patch("/api/v1/customers/{id}/deactivate", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado com id " + id))
                .andExpect(jsonPath("$.path").value("/api/v1/customers/" + id + "/deactivate"));

        Mockito.verify(customerService, Mockito.times(1)).deactivate(id);
    }
}
