package com.alberdev.study.appointmenthub.api.controllers;

import com.alberdev.study.appointmenthub.api.dto.CustomerRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.CustomerResponseDTO;
import com.alberdev.study.appointmenthub.api.mappers.CustomerMapper;
import com.alberdev.study.appointmenthub.application.services.CustomerService;
import com.alberdev.study.appointmenthub.domain.entities.Customer;
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
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/customers")
public class CustomerController {

    private final CustomerService service;
    private final CustomerMapper mapper;

    public CustomerController(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody CustomerRequestDTO requestDTO) {
        var customer = service.create(mapper.toCustomer(requestDTO));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();

        return ResponseEntity.created(uri).body(mapper.toCustomerResponseDTO(customer));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        var customer = service.findById(id);
        return ResponseEntity.ok(mapper.toCustomerResponseDTO(customer));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody CustomerRequestDTO requestDTO) {
        var customerUpdate = mapper.toCustomerUpdate(requestDTO);
        var updatedCustomer = service.update(id, customerUpdate);
        return ResponseEntity.ok(mapper.toCustomerResponseDTO(updatedCustomer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> listCustomers() {
        List<Customer> result = service.findAll();
        return ResponseEntity.ok(result.stream().map(mapper::toCustomerResponseDTO).toList());
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
