package com.alberdev.study.appointmenthub.infrastructure.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
