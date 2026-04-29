package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ServiceOfferingRepositoryTest {

    @Autowired
    private ServiceOfferingRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar servico com sucesso")
    void shouldSaveServiceOffering() {
        // 1. Arrange: Criar um ServiceOffering usando o Object Mother
        var service = ServiceOfferingTestData.createValidServiceOffering();
        // 2. Act: Salvar pelo repository e limpar o contexto de persistencia
        var savedService = repository.saveAndFlush(service);
        entityManager.clear();
        // 3. Assert: Buscar pelo id e validar os dados principais
        Optional<ServiceOffering> foundService = repository.findById(savedService.getId());
        assertTrue(foundService.isPresent(), "O servico deveria estar presente no banco");

        var result = foundService.get();
        assertNotNull(result.getId());
        assertEquals(service.getName(), result.getName());
        assertEquals(service.getDurationInMinutes(), result.getDurationInMinutes());
        assertEquals(service.getBasePrice(), result.getBasePrice());
        assertTrue(result.isActive());
    }
}
