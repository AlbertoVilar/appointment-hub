package com.alberdev.study.appointmenthub.domain.entities;

public final class CustomerTestData {

    private CustomerTestData() {
    }

    public static Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setName("Joao Silva");
        customer.setEmail("joao@email.com");
        customer.setPhone("11999999999");
        customer.setActive(true);
        return customer;
    }
}
