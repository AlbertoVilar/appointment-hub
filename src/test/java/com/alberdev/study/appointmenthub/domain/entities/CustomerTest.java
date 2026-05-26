package com.alberdev.study.appointmenthub.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerTest {

    @Test
    void shouldCreateCustomerActiveByDefault() {
        Customer customer = new Customer(null, "Joao Silva", "joao@email.com", "11999999999");

        assertTrue(customer.isActive());
    }
}
