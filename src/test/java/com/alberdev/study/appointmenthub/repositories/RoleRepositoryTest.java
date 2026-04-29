package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Role;
import com.alberdev.study.appointmenthub.domain.entities.RoleTestData;
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
class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar role com sucesso")
    void shouldSaveRole() {
        Role role = RoleTestData.createProfessionalRole();

        Role savedRole = repository.saveAndFlush(role);
        entityManager.clear();

        Optional<Role> foundRole = repository.findById(savedRole.getId());

        assertTrue(foundRole.isPresent(), "A role deveria estar presente no banco");

        Role result = foundRole.get();
        assertNotNull(result.getId(), "O ID deveria ter sido gerado");
        assertEquals(role.getName(), result.getName(), "O nome da role deve ser o mesmo");
    }
}
