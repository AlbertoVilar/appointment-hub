package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createValidServiceOffering;
import static org.junit.jupiter.api.Assertions.*;


class ServiceOfferingTest {

    // =========================
    // Activate ServiceOffering
    // =========================
    @Test
    @DisplayName("Deve ativar o serviço inativo")
    void shouldActivateInactiveServiceOffering() {

        var serviceOffering = createValidServiceOffering();
        serviceOffering.setActive(false);

        serviceOffering.activate();

        assertTrue(serviceOffering.isActive());

    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar serviço ja ativo")
    void shouldThrowExceptionWhenActivatingAlreadyActiveServiceOfferin() {

        var serviceOffering = createValidServiceOffering();

        assertThrows(DomainException.class, serviceOffering::activate);

        assertTrue(serviceOffering.isActive());

    }

    // =========================
    // Deactivate ServiceOffering
    // =========================

    @Test
    @DisplayName("Deve desativar serviço ativo")
    void shouldDeactivateActiveServiceOffering() {

        var serviceOffering = createValidServiceOffering();

        serviceOffering.deactivate();

        assertFalse(serviceOffering.isActive());
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar servico ja inativo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveServiceOffering() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        // 2. Arrange: deixar o ServiceOffering inativo
        serviceOffering.setActive(false);
        // 3. Act & Assert: chamar serviceOffering.deactivate() e esperar DomainException
        assertThrows(DomainException.class, serviceOffering::deactivate);
        // 4. Assert: validar que serviceOffering.isActive() continua false
        assertFalse(serviceOffering.isActive());
    }

    // =========================
    // Update base price
    // =========================

    @Test
    @DisplayName("Deve atualizar preco base quando valor for valido")
    void shouldUpdateBasePriceWhenValueIsValid() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        // 2. Arrange: definir um novo preco base valido
        var newPrice = new BigDecimal("200.00");
        // 3. Act: chamar serviceOffering.updateBasePrice(newPrice)
        serviceOffering.updateBasePrice(newPrice);
        // 4. Assert: validar que serviceOffering.getBasePrice() recebeu o novo preco
        assertEquals(newPrice, serviceOffering.getBasePrice());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar preco base com null")
    void shouldThrowExceptionWhenUpdatingBasePriceWithNull() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        var originalPrice = serviceOffering.getBasePrice();
        // 2. Act & Assert: chamar serviceOffering.updateBasePrice(null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOffering.updateBasePrice(null));
        // 3. Assert: validar que o preco base original foi preservado
        assertEquals(originalPrice, serviceOffering.getBasePrice());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar preco base com zero")
    void shouldThrowExceptionWhenUpdatingBasePriceWithZero() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        var originalPrice = serviceOffering.getBasePrice();
        // 2. Arrange: definir preco base zero
        var newPrice = new BigDecimal("0.00");
        // 3. Act & Assert: chamar serviceOffering.updateBasePrice(newPrice) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOffering.updateBasePrice(newPrice));
        // 4. Assert: validar que o preco base original foi preservado
        assertEquals(originalPrice, serviceOffering.getBasePrice());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar preco base com valor negativo")
    void shouldThrowExceptionWhenUpdatingBasePriceWithNegativeValue() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        var originalPrice = serviceOffering.getBasePrice();
        // 2. Arrange: definir preco base negativo
        var newPrice = new BigDecimal("-10.00");
        // 3. Act & Assert: chamar serviceOffering.updateBasePrice(newPrice) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOffering.updateBasePrice(newPrice));
        // 4. Assert: validar que o preco base original foi preservado
        assertEquals(originalPrice, serviceOffering.getBasePrice());
    }

    // =========================
    // Update duration
    // =========================

    @Test
    @DisplayName("Deve atualizar duracao quando valor for valido")
    void shouldUpdateDurationWhenValueIsValid() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        // 2. Arrange: definir uma nova duracao valida
        var newDurationInMinutes = 90;
        // 3. Act: chamar serviceOffering.updateDuration(newDurationInMinutes)
        serviceOffering.updateDuration(newDurationInMinutes);
        // 4. Assert: validar que serviceOffering.getDurationInMinutes() recebeu a nova duracao
        assertEquals(newDurationInMinutes, serviceOffering.getDurationInMinutes());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar duracao com null")
    void shouldThrowExceptionWhenUpdatingDurationWithNull() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        var originalDurationInMinutes = serviceOffering.getDurationInMinutes();
        // 2. Act & Assert: chamar serviceOffering.updateDuration(null) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOffering.updateDuration(null));
        // 3. Assert: validar que a duracao original foi preservada
        assertEquals(originalDurationInMinutes, serviceOffering.getDurationInMinutes());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar duracao com zero")
    void shouldThrowExceptionWhenUpdatingDurationWithZero() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        var originalDurationInMinutes = serviceOffering.getDurationInMinutes();
        // 2. Arrange: definir duracao zero
        var newDurationInMinutes = 0;
        // 3. Act & Assert: chamar serviceOffering.updateDuration(newDurationInMinutes) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOffering.updateDuration(newDurationInMinutes));
        // 4. Assert: validar que a duracao original foi preservada
        assertEquals(originalDurationInMinutes, serviceOffering.getDurationInMinutes());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar duracao com valor negativo")
    void shouldThrowExceptionWhenUpdatingDurationWithNegativeValue() {
        // 1. Arrange: criar ServiceOffering valido
        var serviceOffering = createValidServiceOffering();
        var originalDurationInMinutes = serviceOffering.getDurationInMinutes();
        // 2. Arrange: definir duracao negativa
        var newDurationInMinutes = -30;
        // 3. Act & Assert: chamar serviceOffering.updateDuration(newDurationInMinutes) e esperar DomainException
        assertThrows(DomainException.class, () -> serviceOffering.updateDuration(newDurationInMinutes));
        // 4. Assert: validar que a duracao original foi preservada
        assertEquals(originalDurationInMinutes, serviceOffering.getDurationInMinutes());
    }
}
