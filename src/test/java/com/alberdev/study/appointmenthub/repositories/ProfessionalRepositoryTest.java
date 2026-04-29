package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Professional;
import com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData;
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
class ProfessionalRepositoryTest {

    @Autowired
    private ProfessionalRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar profissional com sucesso")
    void shouldSaveProfessional() {
        // 1. Arrange: Criar um Professional usando seu Object Mother
        // Dica: no mapeamento atual, AppUser nao e obrigatorio para salvar Professional.
        var professional = ProfessionalTestData.createValidProfessional();

        // 2. Act
        var savedProfessional = repository.saveAndFlush(professional);
        entityManager.clear();

        // 3. Assert
        Optional<Professional> foundProfessional = repository.findById(savedProfessional.getId());

        assertTrue(foundProfessional.isPresent(), "O profissional deveria estar presente no banco");

        var result = foundProfessional.get();
        assertNotNull(result.getId());
        assertEquals("Dra. Ana Costa", result.getName());
        assertEquals("Fisioterapia", result.getSpecialty());
        assertTrue(result.isActive());

    }
}
