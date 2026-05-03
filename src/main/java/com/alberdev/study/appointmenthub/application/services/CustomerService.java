package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Customer;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) {
        var customerOptional = customerRepository.findByEmail(customer.getEmail());

        if (customerOptional.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Ja existe um cliente registrado com o e-mail: " + customer.getEmail()
            );
        }

        return customerRepository.save(customer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente nao encontrado com id = " + id)
        );
    }

    public List<Customer> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customers;
    }

    public Customer update(Long id, Customer customerUpdate) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente nao encontrado com id = " + id)
        );

        if (customerUpdate.getEmail() != null && !customerUpdate.getEmail().equals(customer.getEmail())) {
            customerRepository.findByEmail(customerUpdate.getEmail()).ifPresent(existingCustomer -> {
                if (!existingCustomer.getId().equals(id)) {
                    throw new ResourceAlreadyExistsException(
                            "Ja existe outro cliente registrado com esse e-mail: " + customerUpdate.getEmail()
                    );
                }
            });
        }

        customer.setName(customerUpdate.getName() != null ? customerUpdate.getName() : customer.getName());
        customer.setEmail(customerUpdate.getEmail() != null ? customerUpdate.getEmail() : customer.getEmail());
        customer.setPhone(customerUpdate.getPhone() != null ? customerUpdate.getPhone() : customer.getPhone());

        return customerRepository.save(customer);
    }

    public void activate(Long id) {
        Customer customer = findById(id);
        customer.activate();
        customerRepository.save(customer);
    }

    public void deactivate(Long id) {
        Customer customer = findById(id);
        customer.deactivate();
        customerRepository.save(customer);
    }
}
