package com.alberdev.study.appointmenthub.api.mappers;

import com.alberdev.study.appointmenthub.api.dto.CustomerRequestDTO;
import com.alberdev.study.appointmenthub.api.dto.CustomerResponseDTO;
import com.alberdev.study.appointmenthub.domain.entities.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDTO toCustomerResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isActive()
        );
    }

    public Customer toCustomer(CustomerRequestDTO requestDTO) {
        return new Customer(
                null,
                requestDTO.name(),
                requestDTO.email(),
                requestDTO.phone()
        );
    }

    public Customer toCustomerUpdate(CustomerRequestDTO requestDTO) {
        Customer customer = new Customer();
        customer.setName(requestDTO.name());
        customer.setEmail(requestDTO.email());
        customer.setPhone(requestDTO.phone());
        return customer;
    }

}
