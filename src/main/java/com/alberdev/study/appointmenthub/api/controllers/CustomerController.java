package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.CustomerRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.CustomerResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.CustomerMapper;
import com.alberdev.study.appointmenthub.application.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/customers")
@Tag(name = "Customers", description = "Operações para gerenciamento de clientes.")
public class CustomerController {

    private final CustomerService service;
    private final CustomerMapper mapper;

    public CustomerController(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido."),
            @ApiResponse(responseCode = "409", description = "Já existe cliente cadastrado com os dados informados.")
    })
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Valid CustomerRequestDTO requestDTO) {
        var customer = service.create(mapper.toCustomer(requestDTO));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();

        return ResponseEntity.created(uri).body(mapper.toCustomerResponseDTO(customer));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Busca um cliente por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    })
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        var customer = service.findById(id);
        return ResponseEntity.ok(mapper.toCustomerResponseDTO(customer));
    }

    @PatchMapping(value = "/{id}")
    @Operation(summary = "Atualiza os dados de um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Payload inválido."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "409", description = "Já existe cliente cadastrado com os dados informados.")
    })
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody @Valid CustomerRequestDTO requestDTO) {
        var customerUpdate = mapper.toCustomerUpdate(requestDTO);
        var updatedCustomer = service.update(id, customerUpdate);
        return ResponseEntity.ok(mapper.toCustomerResponseDTO(updatedCustomer));
    }

    @GetMapping
    @Operation(summary = "Lista clientes de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso.")
    })
    public ResponseEntity<Page<CustomerResponseDTO>> listCustomers(Pageable pageable) {
        var customers = service.findAll(pageable);
        return ResponseEntity.ok(customers.map(mapper::toCustomerResponseDTO));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativa um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente ativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o estado atual do cliente."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    })
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativa um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente desativado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Operação inválida para o estado atual do cliente."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    })
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
