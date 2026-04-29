package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.AppUser;
import com.alberdev.study.appointmenthub.domain.entities.AppUserTestData;
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
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar usuario com role com sucesso")
    void shouldSaveAppUserWithRole() {
        // 1. Arrange: Criar e salvar uma Role primeiro
        // Dica: use RoleTestData.createProfessionalRole()
        var roleTest = RoleTestData.createProfessionalRole();
        var savedRole = roleRepository.saveAndFlush(roleTest);

        // 2. Arrange: Criar AppUser e adicionar a Role salva
        // Dica: use AppUserTestData.createValidAppUser()
        var userTest = AppUserTestData.createValidAppUser();
        userTest.addRole(savedRole);

        // 3. Act: Salvar AppUser pelo repository e limpar o contexto
        var newAppUser = repository.saveAndFlush(userTest);
        entityManager.clear();

        // 4. Assert: Buscar AppUser pelo id e validar dados principais e roles
        Optional<AppUser> foundUser = repository.findById(newAppUser.getId());

        assertTrue(foundUser.isPresent(), "O usuario deveria estar presente no banco");

        AppUser result = foundUser.get();
        assertNotNull(result.getId(), "O ID deveria ter sido gerado");
        assertEquals(userTest.getUsername(), result.getUsername(), "O username deve ser o mesmo");
        assertTrue(result.isEnabled(), "O usuario deveria estar habilitado");
        assertEquals(1, result.getRoles().size(), "O usuario deveria ter uma role");
        assertTrue(
                result.getRoles().stream().map(Role::getName).anyMatch(savedRole.getName()::equals),
                "O usuario deveria conter a role salva"
        );
    }
}
