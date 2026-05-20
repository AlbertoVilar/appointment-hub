package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.ServiceOffering;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createValidServiceOffering;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ServiceOfferingServiceTest {

    @Mock
    private ServiceOfferingRepository serviceOfferingRepository;

    @InjectMocks
    private ServiceOfferingService serviceOfferingService;

    // =========================
    // Create ServiceOffering
    // =========================

    @Test
    @DisplayName("Deve criar servico quando dados forem validos")
    void shouldCreateServiceOfferingWhenDataIsValid() {
        // 1. Arrange: criar ServiceOffering valido usando ServiceOfferingTestData
        var serviceOffering = createValidServiceOffering();
        // 2. Arrange: simular repository.existsByName retornando false
        Mockito.when(serviceOfferingRepository.existsByName(serviceOffering.getName())).thenReturn(false);
        // 3. Arrange: simular repository.save retornando o ServiceOffering salvo
        Mockito.when(serviceOfferingRepository.save(serviceOffering)).thenReturn(serviceOffering);
        // 4. Act: chamar serviceOfferingService.create(serviceOffering)
        var result = serviceOfferingService.create(serviceOffering);
        // 5. Assert: validar dados principais retornados
        assertEquals(serviceOffering.getName(), result.getName());
        assertEquals(serviceOffering.getBasePrice(), result.getBasePrice());
        assertEquals(serviceOffering.getDurationInMinutes(), result.getDurationInMinutes());
        // 6. Verify: verificar chamadas ao repository
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).existsByName(serviceOffering.getName());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(serviceOffering);
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar servico nulo")
    void shouldThrowExceptionWhenCreatingNullServiceOffering() {
        // 1. Arrange: nao criar ServiceOffering, pois a entrada sera null
        // 2. Act & Assert: chamar serviceOfferingService.create(null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.create(null));
        // 3. Verify: verificar que existsByName e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar servico com nome ja cadastrado")
    void shouldThrowExceptionWhenCreatingServiceOfferingWithExistingName() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        // 2. Arrange: simular repository.existsByName retornando true
        Mockito.when(serviceOfferingRepository.existsByName(serviceOffering.getName())).thenReturn(true);
        // 3. Act & Assert: chamar serviceOfferingService.create(serviceOffering) e esperar ResourceAlreadyExistsException
        assertThrows(ResourceAlreadyExistsException.class, () -> serviceOfferingService.create(serviceOffering));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).existsByName(serviceOffering.getName());
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    // =========================
    // Find ServiceOffering by id
    // =========================

    @Test
    @DisplayName("Deve buscar servico por id quando existir")
    void shouldFindServiceOfferingByIdWhenExists() {
        // 1. Arrange: criar ServiceOffering valido com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: simular repository.findById retornando Optional.of(serviceOffering)
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 3. Act: chamar serviceOfferingService.findById(id)
        var result = serviceOfferingService.findById(serviceOffering.getId());
        // 4. Assert: validar que retornou o servico esperado
        assertEquals(serviceOffering.getId(), result.getId());
        assertEquals(serviceOffering.getName(), result.getName());
        // 5. Verify: verificar chamada ao repository.findById
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar servico com id nulo")
    void shouldThrowExceptionWhenFindingServiceOfferingWithNullId() {
        // 1. Arrange: definir id null
        Long id = null;
        // 2. Act & Assert: chamar serviceOfferingService.findById(null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.findById(id));
        // 3. Verify: verificar que repository.findById nao foi chamado
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar servico inexistente por id")
    void shouldThrowExceptionWhenFindingServiceOfferingThatDoesNotExist() {
        // 1. Arrange: definir id valido
        Long id = 1L;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(serviceOfferingRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar serviceOfferingService.findById(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> serviceOfferingService.findById(id));
        // 4. Verify: verificar chamada ao repository.findById
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(id);
    }

    // =========================
    // Find all ServiceOfferings
    // =========================

    @Test
    @DisplayName("Deve listar todos os servicos")
    void shouldFindAllServiceOfferings() {
        // 1. Arrange: criar lista com ServiceOfferings validos
        var firstServiceOffering = createValidServiceOffering();

        var secondServiceOffering = createValidServiceOffering();
        secondServiceOffering.setName("Retorno");

        var serviceOfferings = List.of(firstServiceOffering, secondServiceOffering);
        Pageable pageable = PageRequest.of(0, 10);

        // 2. Arrange: simular repository.findAll retornando uma pagina
        Mockito.when(serviceOfferingRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(serviceOfferings, pageable, serviceOfferings.size()));
        // 3. Act: chamar serviceOfferingService.findAll(pageable)
        var result = serviceOfferingService.findAll(pageable);
        // 4. Assert: validar tamanho e dados retornados
        assertEquals(2, result.getContent().size());
        assertEquals(firstServiceOffering.getName(), result.getContent().get(0).getName());
        assertEquals(secondServiceOffering.getName(), result.getContent().get(1).getName());
        // 5. Verify: verificar chamada ao repository.findAll
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findAll(pageable);
    }

    // =========================
    // Update ServiceOffering
    // =========================

    @Test
    @DisplayName("Deve atualizar servico quando dados forem validos")
    void shouldUpdateServiceOfferingWhenDataIsValid() {
        // 1. Arrange: criar ServiceOffering existente com id
        var firstServiceOffering = createValidServiceOffering();
        firstServiceOffering.setId(1L);

        // 2. Arrange: criar ServiceOffering input com novos dados
        var secondServiceOffering = createValidServiceOffering();
        secondServiceOffering.setName("Retorno");
        secondServiceOffering.setBasePrice(new BigDecimal("200.00"));
        secondServiceOffering.setDurationInMinutes(30);

        // 3. Arrange: simular repository.findById retornando o existente
        Mockito.when(serviceOfferingRepository.findById(firstServiceOffering.getId()))
                .thenReturn(Optional.of(firstServiceOffering));
        // 4. Arrange: simular repository.save retornando o existente atualizado
        Mockito.when(serviceOfferingRepository.save(firstServiceOffering)).thenReturn(firstServiceOffering);
        // 5. Act: chamar serviceOfferingService.updateServiceOffering(id, input)
        var result = serviceOfferingService.updateServiceOffering(firstServiceOffering.getId(), secondServiceOffering);
        // 6. Assert: validar name, basePrice e durationInMinutes atualizados
        assertEquals(secondServiceOffering.getName(), result.getName());
        assertEquals(secondServiceOffering.getBasePrice(), result.getBasePrice());
        assertEquals(secondServiceOffering.getDurationInMinutes(), result.getDurationInMinutes());
        // 7. Verify: verificar chamadas ao repository
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(firstServiceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(firstServiceOffering);
    }

    @Test
    @DisplayName("Deve manter dados antigos quando update receber campos nulos")
    void shouldKeepCurrentDataWhenUpdateFieldsAreNull() {
        // 1. Arrange: criar ServiceOffering existente com id, nome, preco e duracao
        var existingServiceOffering = createValidServiceOffering();
        existingServiceOffering.setId(1L);
        var originalName = existingServiceOffering.getName();
        var originalDurationInMinutes = existingServiceOffering.getDurationInMinutes();
        var originalBasePrice = existingServiceOffering.getBasePrice();
        // 2. Arrange: criar ServiceOffering input com campos nulos
        var input = new ServiceOffering();
        // 3. Arrange: simular repository.findById retornando o existente
        Mockito.when(serviceOfferingRepository.findById(existingServiceOffering.getId())).thenReturn(Optional.of(existingServiceOffering));
        // 4. Arrange: simular repository.save retornando o existente
        Mockito.when(serviceOfferingRepository.save(existingServiceOffering)).thenReturn(existingServiceOffering);
        // 5. Act: chamar serviceOfferingService.updateServiceOffering(id, input)
        var result = serviceOfferingService.updateServiceOffering(existingServiceOffering.getId(), input);

        // 6. Assert: validar que os dados originais foram mantidos
        assertAll("Verificar se os campos permanecem os mesmos do helper",
                () -> assertEquals(existingServiceOffering.getId(), result.getId(), "O ID nao deveria mudar"),
                () -> assertEquals(originalName, result.getName(), "O nome deveria continuar o mesmo"),
                () -> assertEquals(originalDurationInMinutes, result.getDurationInMinutes(), "A duracao nao deveria mudar"),

                // DICA: Use compareTo para BigDecimal para evitar problemas de precisão/escala
                () -> assertTrue(originalBasePrice.compareTo(result.getBasePrice()) == 0,
                        "O preco deveria continuar o mesmo")
        );

        // 7. Verify: verificar chamadas ao repository
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(existingServiceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(existingServiceOffering);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar servico com id nulo")
    void shouldThrowExceptionWhenUpdatingServiceOfferingWithNullId() {
        // 1. Arrange: criar ServiceOffering input valido
        var input = createValidServiceOffering();
        // 2. Act & Assert: chamar updateServiceOffering(null, input) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.updateServiceOffering(null, input));
        // 3. Verify: verificar que repository.findById e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar servico nulo")
    void shouldThrowExceptionWhenUpdatingNullServiceOffering() {
        // 1. Arrange: definir id valido
        Long id = 1L;
        // 2. Act & Assert: chamar updateServiceOffering(id, null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.updateServiceOffering(id, null));
        // 3. Verify: verificar que repository.findById e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar servico inexistente")
    void shouldThrowExceptionWhenUpdatingServiceOfferingThatDoesNotExist() {
        // 1. Arrange: definir id valido
        Long id = 1L;
        // 2. Arrange: criar ServiceOffering input valido
        var input = createValidServiceOffering();
        // 3. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(serviceOfferingRepository.findById(id)).thenReturn(Optional.empty());
        // 4. Act & Assert: chamar updateServiceOffering(id, input) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> serviceOfferingService.updateServiceOffering(id, input));
        // 5. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(id);
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    // =========================
    // Activate ServiceOffering
    // =========================

    @Test
    @DisplayName("Deve ativar servico inativo")
    void shouldActivateInactiveServiceOffering() {
        // 1. Arrange: criar ServiceOffering inativo com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        serviceOffering.setActive(false);
        // 2. Arrange: simular repository.findById retornando esse ServiceOffering
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 3. Arrange: simular repository.save retornando o ServiceOffering ativado
        Mockito.when(serviceOfferingRepository.save(serviceOffering)).thenReturn(serviceOffering);
        // 4. Act: chamar serviceOfferingService.activate(id)
        serviceOfferingService.activate(serviceOffering.getId());
        // 5. Assert: validar que serviceOffering.isActive() ficou true
        assertTrue(serviceOffering.isActive());
        // 6. Verify: verificar que save foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(serviceOffering);
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar servico ja ativo")
    void shouldThrowExceptionWhenActivatingAlreadyActiveServiceOffering() {
        // 1. Arrange: criar ServiceOffering ativo com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: simular repository.findById retornando esse ServiceOffering
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 3. Act & Assert: chamar serviceOfferingService.activate(id) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.activate(serviceOffering.getId()));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar servico com id nulo")
    void shouldThrowExceptionWhenActivatingServiceOfferingWithNullId() {
        // 1. Arrange: definir id null
        Long id = null;
        // 2. Act & Assert: chamar serviceOfferingService.activate(null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.activate(id));
        // 3. Verify: verificar que repository.findById e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar servico inexistente")
    void shouldThrowExceptionWhenActivatingServiceOfferingThatDoesNotExist() {
        // 1. Arrange: definir id valido
        Long id = 1L;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(serviceOfferingRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar serviceOfferingService.activate(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> serviceOfferingService.activate(id));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(id);
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    // =========================
    // Deactivate ServiceOffering
    // =========================

    @Test
    @DisplayName("Deve desativar servico ativo")
    void shouldDeactivateActiveServiceOffering() {
        // 1. Arrange: criar ServiceOffering ativo com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: simular repository.findById retornando esse ServiceOffering
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 3. Arrange: simular repository.save retornando o ServiceOffering desativado
        Mockito.when(serviceOfferingRepository.save(serviceOffering)).thenReturn(serviceOffering);
        // 4. Act: chamar serviceOfferingService.deactivate(id)
        serviceOfferingService.deactivate(serviceOffering.getId());
        // 5. Assert: validar que serviceOffering.isActive() ficou false
        assertFalse(serviceOffering.isActive());
        // 6. Verify: verificar que save foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(serviceOffering);
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar servico ja inativo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveServiceOffering() {
        // 1. Arrange: criar ServiceOffering inativo com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        serviceOffering.setActive(false);
        // 2. Arrange: simular repository.findById retornando esse ServiceOffering
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 3. Act & Assert: chamar serviceOfferingService.deactivate(id) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.deactivate(serviceOffering.getId()));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar servico com id nulo")
    void shouldThrowExceptionWhenDeactivatingServiceOfferingWithNullId() {
        // 1. Arrange: definir id null
        Long id = null;
        // 2. Act & Assert: chamar serviceOfferingService.deactivate(null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.deactivate(id));
        // 3. Verify: verificar que repository.findById e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar servico inexistente")
    void shouldThrowExceptionWhenDeactivatingServiceOfferingThatDoesNotExist() {
        // 1. Arrange: definir id valido
        Long id = 1L;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(serviceOfferingRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar serviceOfferingService.deactivate(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> serviceOfferingService.deactivate(id));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(id);
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    // =========================
    // Update base price
    // =========================

    @Test
    @DisplayName("Deve atualizar preco base do servico quando valor for valido")
    void shouldUpdateBasePriceWhenValueIsValid() {
        // 1. Arrange: criar ServiceOffering existente com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: definir novo preco base valido
        var newPrice = new BigDecimal("200.00");
        // 3. Arrange: simular repository.findById retornando o existente
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 4. Arrange: simular repository.save retornando o ServiceOffering atualizado
        Mockito.when(serviceOfferingRepository.save(serviceOffering)).thenReturn(serviceOffering);
        // 5. Act: chamar serviceOfferingService.updateBasePrice(id, newPrice)
        var result = serviceOfferingService.updateBasePrice(serviceOffering.getId(), newPrice);
        // 6. Assert: validar que o preco base foi atualizado
        assertEquals(newPrice, result.getBasePrice());
        // 7. Verify: verificar chamadas ao repository
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(serviceOffering);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar preco base com id nulo")
    void shouldThrowExceptionWhenUpdatingBasePriceWithNullId() {
        // 1. Arrange: definir novo preco base valido
        var newPrice = new BigDecimal("200.00");
        // 2. Act & Assert: chamar updateBasePrice(null, newPrice) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.updateBasePrice(null, newPrice));
        // 3. Verify: verificar que repository.findById e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar preco base de servico inexistente")
    void shouldThrowExceptionWhenUpdatingBasePriceOfServiceOfferingThatDoesNotExist() {
        // 1. Arrange: definir id valido e novo preco base valido
        Long id = 1L;
        var newPrice = new BigDecimal("200.00");
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(serviceOfferingRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar updateBasePrice(id, newPrice) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> serviceOfferingService.updateBasePrice(id, newPrice));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(id);
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar preco base com valor invalido")
    void shouldThrowExceptionWhenUpdatingBasePriceWithInvalidValue() {
        // 1. Arrange: criar ServiceOffering existente com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: definir novo preco base invalido
        var invalidPrice = new BigDecimal("0.00");
        // 3. Arrange: simular repository.findById retornando o existente
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 4. Act & Assert: chamar updateBasePrice(id, invalidPrice) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.updateBasePrice(serviceOffering.getId(), invalidPrice));
        // 5. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));

    }

    // =========================
    // Update duration
    // =========================

    @Test
    @DisplayName("Deve atualizar duracao do servico quando valor for valido")
    void shouldUpdateDurationWhenValueIsValid() {
        // 1. Arrange: criar ServiceOffering existente com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: definir nova duracao valida
        var newDurationInMinutes = 90;
        // 3. Arrange: simular repository.findById retornando o existente
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 4. Arrange: simular repository.save retornando o ServiceOffering atualizado
        Mockito.when(serviceOfferingRepository.save(serviceOffering)).thenReturn(serviceOffering);
        // 5. Act: chamar serviceOfferingService.updateDuration(id, newDurationInMinutes)
        var result = serviceOfferingService.updateDuration(serviceOffering.getId(), newDurationInMinutes);
        // 6. Assert: validar que a duracao foi atualizada
        assertEquals(newDurationInMinutes, result.getDurationInMinutes());
        // 7. Verify: verificar chamadas ao repository
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).save(serviceOffering);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar duracao com id nulo")
    void shouldThrowExceptionWhenUpdatingDurationWithNullId() {
        // 1. Arrange: definir nova duracao valida
        var newDurationInMinutes = 90;
        // 2. Act & Assert: chamar updateDuration(null, newDurationInMinutes) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.updateDuration(null, newDurationInMinutes));
        // 3. Verify: verificar que repository.findById e save nunca foram chamados
        verifyNoInteractions(serviceOfferingRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar duracao de servico inexistente")
    void shouldThrowExceptionWhenUpdatingDurationOfServiceOfferingThatDoesNotExist() {
        // 1. Arrange: definir id valido e nova duracao valida
        Long id = 1L;
        var newDurationInMinutes = 90;
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(serviceOfferingRepository.findById(id)).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar updateDuration(id, newDurationInMinutes) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> serviceOfferingService.updateDuration(id, newDurationInMinutes));
        // 4. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(id);
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar duracao com valor invalido")
    void shouldThrowExceptionWhenUpdatingDurationWithInvalidValue() {
        // 1. Arrange: criar ServiceOffering existente com id
        var serviceOffering = createValidServiceOffering();
        serviceOffering.setId(1L);
        // 2. Arrange: definir duracao invalida
        var invalidDurationInMinutes = 0;
        // 3. Arrange: simular repository.findById retornando o existente
        Mockito.when(serviceOfferingRepository.findById(serviceOffering.getId())).thenReturn(Optional.of(serviceOffering));
        // 4. Act & Assert: chamar updateDuration(id, invalidDuration) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOfferingService.updateDuration(serviceOffering.getId(), invalidDurationInMinutes));
        // 5. Verify: verificar que save nunca foi chamado
        Mockito.verify(serviceOfferingRepository, Mockito.times(1)).findById(serviceOffering.getId());
        Mockito.verify(serviceOfferingRepository, Mockito.never()).save(Mockito.any(ServiceOffering.class));
    }

}
