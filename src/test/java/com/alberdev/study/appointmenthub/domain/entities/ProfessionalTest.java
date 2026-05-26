package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createValidProfessional;
import static org.junit.jupiter.api.Assertions.*;

class ProfessionalTest {

    // =========================
    // Activate professional
    // =========================

    @Test
    @DisplayName("Deve ativar profissional inativo")
    void shouldActivateInactiveProfessional() {
        // 1. Arrange: criar Professional valido
        var professional = createValidProfessional();
        // 2. Arrange: deixar o Professional inativo
        professional.setActive(false);
        // 3. Act: chamar professional.activate()
        professional.activate();
        // 4. Assert: validar que professional.isActive() ficou true
        assertTrue(professional.isActive());
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar profissional ja ativo")
    void shouldThrowExceptionWhenActivatingAlreadyActiveProfessional() {
        // 1. Arrange: criar Professional valido ja ativo
        var professional = createValidProfessional();
        // 2. Act & Assert: chamar professional.activate() e esperar DomainException
        assertThrows(DomainException.class, professional::activate);
        // 3. Assert: validar que professional.isActive() continua true
        assertTrue(professional.isActive());
    }

    // =========================
    // Deactivate professional
    // =========================

    @Test
    @DisplayName("Deve desativar profissional ativo")
    void shouldDeactivateActiveProfessional() {
        // 1. Arrange: criar Professional valido ja ativo
        var professional = createValidProfessional();
        // 2. Act: chamar professional.deactivate()
        professional.deactivate();
        // 3. Assert: validar que professional.isActive() ficou false
        assertFalse(professional.isActive());
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar profissional ja inativo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveProfessional() {
        // 1. Arrange: criar Professional valido
        var professional = createValidProfessional();
        // 2. Arrange: deixar o Professional inativo
        professional.setActive(false);
        // 3. Act & Assert: chamar professional.deactivate() e esperar DomainException
        assertThrows(DomainException.class, professional::deactivate);
        // 4. Assert: validar que professional.isActive() continua false
        assertFalse(professional.isActive());
    }

    // =========================
    // Update specialty
    // =========================

    @Test
    @DisplayName("Deve atualizar especialidade quando valor for valido")
    void shouldUpdateSpecialtyWhenValueIsValid() {
        // 1. Arrange: criar Professional valido
        var professional = createValidProfessional();
        // 2. Arrange: definir uma nova especialidade valida
        String newSpecialty = "Cardiologia";
        // 3. Act: chamar professional.updateSpecialty(newSpecialty)
        professional.updateSpecialty(newSpecialty);
        // 4. Assert: validar que professional.getSpecialty() recebeu a nova especialidade
        assertEquals("Cardiologia", professional.getSpecialty());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar especialidade com null")
    void shouldThrowExceptionWhenUpdatingSpecialtyWithNull() {
        // 1. Arrange: criar Professional valido
        var professional = createValidProfessional();
        // 2. Act & Assert: chamar professional.updateSpecialty(null) e esperar DomainException
        assertThrows(DomainException.class, () -> professional.updateSpecialty(null));
        // 3. Assert: validar que a especialidade original foi preservada
        assertEquals("Fisioterapia", professional.getSpecialty());
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar especialidade em branco")
    void shouldThrowExceptionWhenUpdatingSpecialtyWithBlankValue() {
        // 1. Arrange: criar Professional valido
        var professional = createValidProfessional();
        // 2. Act & Assert: chamar professional.updateSpecialty(null) e esperar DomainException
        assertThrows(DomainException.class, () -> professional.updateSpecialty(" "));
        // 3. Assert: validar que a especialidade original foi preservada
        assertEquals("Fisioterapia", professional.getSpecialty());
    }
}
