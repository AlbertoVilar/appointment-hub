package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.api.dto.CustomerRequestDTO;

public final class CustomerTestData {

    private CustomerTestData() {
    }

    public static Customer createValidCustomer() {
        return new Customer(null, "Joao Silva", "joao@email.com", "11999999999");
    }

    public static CustomerRequestDTO createCustomerRequestDTO() {
        return new CustomerRequestDTO("Joao Silva", "joao@email.com", "11999999999");
    }
}
