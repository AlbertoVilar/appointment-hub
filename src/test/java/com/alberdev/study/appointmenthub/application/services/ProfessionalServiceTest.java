package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.AppUserTestData;
import com.alberdev.study.appointmenthub.domain.entities.Professional;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createValidProfessional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ProfessionalService professionalService;

    // =========================
    // Create professional
    // =========================

    @Test
    @DisplayName("Deve criar profissional quando dados forem validos")
    void shouldCreateProfessionalWhenDataIsValid() {
        // 1. Arrange: criar Professional valido usando ProfessionalTestData
        var newProfessional = createValidProfessional();
        // 2. Arrange: simular existsByAppUser retornando false
        Mockito.when(professionalRepository.existsByAppUser(
                newProfessional.getAppUser())).thenReturn(false);
        // 3. Arrange: simular save retornando o Professional salvo
        Mockito.when(professionalRepository.save(newProfessional)).thenReturn(newProfessional);
        // 4. Act: chamar professionalService.create(professional)
        var result = professionalService.create(newProfessional);
        // 5. Assert: validar retorno e se o profissional ficou ativo
        assertTrue(result.isActive());

        // 6. Verify: verificar que existsByAppUser e save foram chamados
        Mockito.verify(professionalRepository, Mockito.times(1)).existsByAppUser(newProfessional.getAppUser());
        Mockito.verify(professionalRepository, Mockito.times(1)).save(newProfessional);

    }

    @Test
    @DisplayName("Deve lancar excecao ao criar profissional nulo")
    void shouldThrowExceptionWhenCreatingNullProfessional() {
        // 1. Arrange: nao criar Professional, pois a entrada sera null
        // 2. Act & Assert: chamar professionalService.create(null) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            professionalService.create(null);
        });

        // 3. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar profissional sem usuario")
    void shouldThrowExceptionWhenCreatingProfessionalWithoutAppUser() {
        // 1. Arrange: criar Professional valido
        var newProfessional = createValidProfessional();
        // 2. Arrange: remover o AppUser do Professional
        newProfessional.setAppUser(null);
        // 3. Act & Assert: chamar professionalService.create(professional) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            professionalService.create(newProfessional);
        });
        // 4. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar profissional com usuario ja vinculado")
    void shouldThrowExceptionWhenCreatingProfessionalWithExistingAppUser() {
        // 1. Arrange: criar Professional valido com AppUser
        var professional = createValidProfessional();
        // 2. Arrange: simular existsByAppUser retornando true
        Mockito.when(professionalRepository.existsByAppUser(professional.getAppUser())).thenReturn(true);
        // 3. Act & Assert: chamar professionalService.create(professional) e esperar ResourceAlreadyExistsException
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            professionalService.create(professional);
        });
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).existsByAppUser(professional.getAppUser());
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    // =========================
    // Find professional
    // =========================

    @Test
    @DisplayName("Deve buscar profissional por id quando existir")
    void shouldFindProfessionalByIdWhenExists() {
        // 1. Arrange: criar Professional valido e definir um id
        var professional = createValidProfessional();
        professional.setId(1L);
        // 2. Arrange: simular repository.findById retornando Optional com Professional
        Mockito.when(professionalRepository.findById(professional.getId())).thenReturn(Optional.of(professional));
        // 3. Act: chamar professionalService.findById(id)
        var result = professionalService.findById(professional.getId());
        // 4. Assert: validar que retornou o profissional esperado
        assertEquals(professional.getId(), result.getId());
        assertEquals(professional.getName(), result.getName());
        // 5. Verify: verificar que findById foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
    }


    @Test
    @DisplayName("Deve lancar excecao ao buscar profissional com id nulo")
    void shouldThrowExceptionWhenFindingProfessionalWithNullId() {
        // 1. Arrange: definir id como null
        Long id = null;
        // 2. Act & Assert: chamar professionalService.findById(null) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            professionalService.findById(id);
        });
        // 3. Verify: verificar que repository.findById nunca foi chamado
        verifyNoInteractions(professionalRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar profissional inexistente por id")
    void shouldThrowExceptionWhenProfessionalByIdDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Long id = 10L;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(professionalRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar professionalService.findById(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            professionalService.findById(id);
        });
        // 4. Verify: verificar que findById foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(id);
    }

    // =========================
    // FindAll professionals
    // =========================
    @Test
    @DisplayName("Deve buscar tods os profissionais quando existir")
    void shouldFindAllProfessionalsWhenExists() {
        var professional = createValidProfessional();

        var anotherProfessional = new Professional();
        anotherProfessional.setName("Dra. Ana Costa ATUALIZADO");
        anotherProfessional.setSpecialty("CARDIOLOGIA");
        anotherProfessional.setAppUser(AppUserTestData.createValidAppUser());

        List<Professional> professionals = List.of(professional, anotherProfessional);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(professionalRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(professionals, pageable, professionals.size()));

        var foundProfessionals = professionalService.findAll(pageable);

        assertEquals(2, foundProfessionals.getContent().size());
        assertEquals(professional, foundProfessionals.getContent().get(0));
        assertEquals(anotherProfessional, foundProfessionals.getContent().get(1));
        Mockito.verify(professionalRepository, Mockito.times(1)).findAll(pageable);
    }

    // =========================
    // Update professional
    // =========================

    @Test
    @DisplayName("Deve atualizar profissional quando dados forem validos")
    void shouldUpdateProfessionalWhenDataIsValid() {
        // 1. Arrange: criar Professional existente com id
        var professional = createValidProfessional();
        professional.setId(1L);

        // 2. Arrange: criar Professional update com novos dados
        var professionalUpdate = new Professional();
        professionalUpdate.setName("Dra. Ana Costa ATUALIZADO");
        professionalUpdate.setSpecialty("CARDIOLOGIA");
        professionalUpdate.setAppUser(AppUserTestData.createValidAppUser());

        // 3. Arrange: simular repository.findById retornando o Professional existente
        Mockito.when(professionalRepository.findById(professional.getId()))
                .thenReturn(Optional.of(professional));

        // 4. Arrange: simular repository.save retornando o Professional atualizado
        Mockito.when(professionalRepository.save(professional))
                .thenReturn(professional);

        // 5. Act: chamar professionalService.update(id, professionalUpdate)
        var result = professionalService.update(professional.getId(), professionalUpdate);

        // 6. Assert: validar name, specialty e appUser atualizados
        assertNotNull(result);
        assertEquals("Dra. Ana Costa ATUALIZADO", result.getName());
        assertEquals("CARDIOLOGIA", result.getSpecialty());
        assertEquals(professionalUpdate.getAppUser(), result.getAppUser());

        // 7. Verify: verificar chamadas a findById e save
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).save(professional);
    }

    @Test
    @DisplayName("Deve manter dados antigos quando update receber campos nulos")
    void shouldKeepCurrentDataWhenUpdateFieldsAreNull() {
        // 1. Arrange: criar Professional existente com name, specialty e appUser
        var professional = createValidProfessional();
        professional.setId(1L);

        // 2. Arrange: criar Professional update com campos nulos
        var professionalUpdate = new Professional();
        // 3. Arrange: simular repository.findById retornando o Professional existente
        Mockito.when(professionalRepository.findById(professional.getId()))
                .thenReturn(Optional.of(professional));
        // 4. Arrange: simular repository.save retornando o Professional preservado
        Mockito.when(professionalRepository.save(professional))
                .thenReturn(professional);
        // 5. Act: chamar professionalService.update(id, professionalUpdate)
        var result = professionalService.update(professional.getId(), professionalUpdate);
        // 6. Assert: validar que name, specialty e appUser antigos foram preservados
        assertNotNull(result);
        assertEquals("Dra. Ana Costa", result.getName());
        assertEquals("Fisioterapia", result.getSpecialty());
        assertEquals(professional.getAppUser(), result.getAppUser());

        // 7. Verify: verificar chamadas a findById e save
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).save(professional);

    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar profissional com id nulo")
    void shouldThrowExceptionWhenUpdatingProfessionalWithNullId() {
        // 1. Arrange: definir id como null
        Long id = null;

        // 2. Arrange: criar Professional update nao nulo
        var professionalUpdate = new Professional();

       // 3. Act & Assert: chamar professionalService.update(null, professionalUpdate) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            professionalService.update(id, professionalUpdate);
        });
        // 4. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);

    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar profissional nulo")
    void shouldThrowExceptionWhenUpdatingNullProfessional() {
        // 1. Arrange: definir um id valido
        Long id = 1L;
        // 2. Arrange: definir professionalUpdate como null
        Professional updateNullProfessional = null;

        // 3. Act & Assert: chamar professionalService.update(id, null) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            professionalService.update(id, updateNullProfessional);
        });
        // 4. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);

    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar profissional inexistente")
    void shouldThrowExceptionWhenUpdatingProfessionalThatDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Long id = 10L;
        // 2. Arrange: criar Professional update valido
        var professionalUpdate = new Professional();
        // 3. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(professionalRepository.findById(id)).thenReturn(Optional.empty());
        // 4. Act & Assert: chamar professionalService.update(id, professionalUpdate) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            professionalService.update(id, professionalUpdate);
        });
        // 5. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(id);
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    // =========================
    // Activate professional
    // =========================

    @Test
    @DisplayName("Deve ativar profissional inativo")
    void shouldActivateInactiveProfessional() {
        // 1. Arrange: criar Professional inativo com id
        var inactiveProfessional = createValidProfessional();
        inactiveProfessional.setId(1L);
        inactiveProfessional.setActive(false);
        // 2. Arrange: simular repository.findById retornando esse Professional
        Mockito.when(professionalRepository.findById(inactiveProfessional.getId())).thenReturn(Optional.of(inactiveProfessional));
        // 3. Arrange: simular repository.save retornando o Professional ativado
        Mockito.when(professionalRepository.save(inactiveProfessional))
                .thenReturn(inactiveProfessional);

        // 4. Act: chamar professionalService.activate(id)
        professionalService.activate(inactiveProfessional.getId());
        // 5. Assert: validar que professional.isActive() ficou true
        assertTrue(inactiveProfessional.isActive());
        // 6. Verify: verificar chamadas a findById e save
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(inactiveProfessional.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).save(inactiveProfessional);
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar profissional ja ativo")
    void shouldThrowExceptionWhenActivatingAlreadyActiveProfessional() {
        // 1. Arrange: criar Professional ativo com id
        var activeProfessional = createValidProfessional();
        activeProfessional.setId(1L);
        // 2. Arrange: simular repository.findById retornando esse Professional
        Mockito.when(professionalRepository.findById(activeProfessional.getId())).thenReturn(Optional.of(activeProfessional));
        // 3. Act & Assert: chamar professionalService.activate(id) e esperar DomainException
        assertThrows(DomainException.class, () -> professionalService.activate(activeProfessional.getId()));
        // 4. Assert: validar que professional.isActive() continua true
        assertTrue(activeProfessional.isActive());
        // 5. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(activeProfessional.getId());
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar profissional com id nulo")
    void shouldThrowExceptionWhenActivatingProfessionalWithNullId() {
        // 1. Arrange: definir id como null
        Long id = null;
        // 2. Act & Assert: chamar professionalService.activate(null) e esperar DomainException
        assertThrows(DomainException.class, () -> professionalService.activate(id));
        // 3. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar profissional inexistente")
    void shouldThrowExceptionWhenActivatingProfessionalThatDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Long id = 10L;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(professionalRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar professionalService.activate(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> professionalService.activate(id));
        // 4. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(id);
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    // =========================
    // Deactivate professional
    // =========================

    @Test
    @DisplayName("Deve desativar profissional ativo")
    void shouldDeactivateActiveProfessional() {
        // 1. Arrange: criar Professional ativo com id
        var activeProfessional = createValidProfessional();
        activeProfessional.setId(1L);
        // 2. Arrange: simular repository.findById retornando esse Professional
        Mockito.when(professionalRepository.findById(activeProfessional.getId())).thenReturn(Optional.of(activeProfessional));
        // 3. Arrange: simular repository.save retornando o Professional desativado
        Mockito.when(professionalRepository.save(activeProfessional))
                .thenReturn(activeProfessional);
        // 4. Act: chamar professionalService.deactivate(id)
        professionalService.deactivate(activeProfessional.getId());
        // 5. Assert: validar que professional.isActive() ficou false
        assertFalse(activeProfessional.isActive());
        // 6. Verify: verificar chamadas a findById e save
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(activeProfessional.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).save(activeProfessional);
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar profissional ja inativo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveProfessional() {
        // 1. Arrange: criar Professional inativo com id
        var inactiveProfessional = createValidProfessional();
        inactiveProfessional.setId(1L);
        inactiveProfessional.setActive(false);
        // 2. Arrange: simular repository.findById retornando esse Professional
        Mockito.when(professionalRepository.findById(inactiveProfessional.getId())).thenReturn(Optional.of(inactiveProfessional));
        // 3. Act & Assert: chamar professionalService.deactivate(id) e esperar DomainException
        assertThrows(DomainException.class, () -> professionalService.deactivate(inactiveProfessional.getId()));
        // 4. Assert: validar que professional.isActive() continua false
        assertFalse(inactiveProfessional.isActive());
        // 5. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(inactiveProfessional.getId());
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar profissional com id nulo")
    void shouldThrowExceptionWhenDeactivatingProfessionalWithNullId() {
        // 1. Arrange: definir id como null
        Long id = null;
        // 2. Act & Assert: chamar professionalService.deactivate(null) e esperar DomainException
        assertThrows(DomainException.class, () -> professionalService.deactivate(id));
        // 3. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar profissional inexistente")
    void shouldThrowExceptionWhenDeactivatingProfessionalThatDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Long id = 10L;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(professionalRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar professionalService.deactivate(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> professionalService.deactivate(id));
        // 4. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(id);
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    // =========================
    // Update professional specialty
    // =========================

    @Test
    @DisplayName("Deve atualizar especialidade do profissional quando dados forem validos")
    void shouldUpdateProfessionalSpecialtyWhenDataIsValid() {
        // 1. Arrange: criar Professional existente com id e especialidade original
        var professional = createValidProfessional();
        professional.setId(1L);
        // 2. Arrange: definir a nova especialidade
        String newSpecialty = "Cardiologia";
        // 3. Arrange: simular repository.findById retornando o Professional existente
        Mockito.when(professionalRepository.findById(professional.getId())).thenReturn(Optional.of(professional));
        // 4. Arrange: simular repository.save retornando o Professional atualizado
        Mockito.when(professionalRepository.save(professional)).thenReturn(professional);
        // 5. Act: chamar professionalService.updateSpecialty(id, specialty)
        var result = professionalService.updateSpecialty(professional.getId(), newSpecialty);
        // 6. Assert: validar que a especialidade foi atualizada
        assertEquals(newSpecialty, result.getSpecialty());
        // 7. Verify: verificar chamadas a findById e save
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(professional.getId());
        Mockito.verify(professionalRepository, Mockito.times(1)).save(professional);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar especialidade com id nulo")
    void shouldThrowExceptionWhenUpdatingSpecialtyWithNullId() {
        // 1. Arrange: definir id como null
        Long id = null;
        // 2. Arrange: definir uma especialidade valida
        String newSpecialty = "Cardiologia";
        // 3. Act & Assert: chamar professionalService.updateSpecialty(null, specialty) e esperar DomainException
        assertThrows(DomainException.class, () -> professionalService.updateSpecialty(id, newSpecialty));
        // 4. Verify: verificar que repository nao foi chamado
        verifyNoInteractions(professionalRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar especialidade de profissional inexistente")
    void shouldThrowExceptionWhenUpdatingSpecialtyOfProfessionalThatDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Long id = 10L;
        // 2. Arrange: definir uma especialidade valida
        String newSpecialty = "Cardiologia";
        // 3. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(professionalRepository.findById(id)).thenReturn(Optional.empty());
        // 4. Act & Assert: chamar professionalService.updateSpecialty(id, specialty) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> professionalService.updateSpecialty(id, newSpecialty));
        // 5. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(id);
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar especialidade com valor invalido")
    void shouldThrowExceptionWhenUpdatingSpecialtyWithInvalidValue() {
        // 1. Arrange: criar Professional existente com id
        Long id = 1L;
        var professional = createValidProfessional();
        professional.setId(id);

        // 2. Arrange: definir especialidade invalida, como null ou texto em branco
        String invalidSpecialty = " ";

        // 3. Arrange: simular repository.findById retornando o Professional existente
        Mockito.when(professionalRepository.findById(professional.getId())).thenReturn(Optional.of(professional));
        // 4. Act & Assert: chamar professionalService.updateSpecialty(id, specialty) e esperar DomainException
        assertThrows(DomainException.class, () -> professionalService.updateSpecialty(id, invalidSpecialty));
        // 5. Assert: validar que a especialidade original foi preservada
        assertEquals("Fisioterapia", professional.getSpecialty());
        // 6. Verify: verificar que findById foi chamado e save nunca foi chamado
        Mockito.verify(professionalRepository, Mockito.times(1)).findById(id);
        Mockito.verify(professionalRepository, Mockito.never()).save(Mockito.any(Professional.class));
    }
}
