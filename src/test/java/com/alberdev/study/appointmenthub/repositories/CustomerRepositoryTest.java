package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Customer;
import com.alberdev.study.appointmenthub.domain.entities.CustomerTestData;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve persistir um cliente com sucesso quando os dados forem validos")
    void shouldPersistCustomerSuccessfully() {
        Customer customer = CustomerTestData.createValidCustomer();

        Customer savedCustomer = repository.saveAndFlush(customer);
        entityManager.clear();

        Optional<Customer> foundCustomer = repository.findById(savedCustomer.getId());

        assertTrue(foundCustomer.isPresent(), "O cliente deveria estar presente no banco");

        Customer result = foundCustomer.get();
        assertNotNull(result.getId(), "O ID deveria ter sido gerado");
        assertEquals(customer.getName(), result.getName(), "O nome deve ser o mesmo");
        assertEquals(customer.getEmail(), result.getEmail(), "O e-mail deve ser o mesmo");
        assertEquals(customer.getPhone(), result.getPhone(), "O telefone deve ser o mesmo");
        assertTrue(result.isActive(), "O cliente deveria estar ativo");
    }
}
