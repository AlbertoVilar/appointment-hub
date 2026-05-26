package com.alberdev.study.appointmenthub.repositories;

import com.alberdev.study.appointmenthub.domain.entities.Appointment;
import com.alberdev.study.appointmenthub.infrastructure.repositories.AppUserRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.AppointmentRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ProfessionalRepository;
import com.alberdev.study.appointmenthub.infrastructure.repositories.ServiceOfferingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.alberdev.study.appointmenthub.domain.entities.AppointmentTestData.createValidScheduledAppointment;
import static com.alberdev.study.appointmenthub.domain.entities.CustomerTestData.createValidCustomer;
import static com.alberdev.study.appointmenthub.domain.entities.ProfessionalTestData.createValidProfessional;
import static com.alberdev.study.appointmenthub.domain.entities.ServiceOfferingTestData.createValidServiceOffering;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar agendamento com cliente, profissional e servico")
    void shouldSaveAppointmentWithRequiredRelationships() {
        // 1. Arrange: Criar um Customer valido usando CustomerTestData
        var customer = createValidCustomer();
        // 2. Arrange: Salvar o Customer, pois Appointment.customer e obrigatorio
        var savedCustomer = customerRepository.saveAndFlush(customer);
        // 3. Arrange: Criar um Professional valido usando ProfessionalTestData
        var professional = createValidProfessional();
        // 4. Arrange: Salvar o Professional, pois Appointment.professional e obrigatorio
        var savedAppUser = appUserRepository.saveAndFlush(professional.getAppUser());
        professional.setAppUser(savedAppUser);
        var savedProfessional = professionalRepository.saveAndFlush(professional);
        // 5. Arrange: Criar um ServiceOffering valido usando ServiceOfferingTestData
        var serviceOffering = createValidServiceOffering();
        // 6. Arrange: Salvar o ServiceOffering, pois Appointment.serviceOffering e obrigatorio
        var savedServiceOffering = serviceOfferingRepository.saveAndFlush(serviceOffering);

        // 7. Arrange: Criar um Appointment valido usando AppointmentTestData
        var appointment = createValidScheduledAppointment();
        
        // 8. Arrange: Substituir no Appointment os relacionamentos pelos objetos ja salvos
        appointment.setCustomer(savedCustomer);
        appointment.setProfessional(savedProfessional);
        appointment.setServiceOffering(savedServiceOffering);

        // 9. Act: Salvar o Appointment pelo repository e limpar o contexto de persistencia
        var newAppointment = repository.saveAndFlush(appointment);
        entityManager.clear();
        // 10. Assert: Buscar o Appointment pelo id
        Optional<Appointment> foundAppointment = repository.findById(newAppointment.getId());
        assertTrue(foundAppointment.isPresent(), "O agendamento deveria estar presente no banco");

        // 11. Assert: Validar dados principais do Appointment
        var result = foundAppointment.get();
        assertNotNull(result.getId(), "O ID deveria ter sido gerado");
        assertEquals(appointment.getScheduledAt(), result.getScheduledAt(), "A data do agendamento deve ser a mesma");
        assertEquals(appointment.getStatus(), result.getStatus(), "O status deve ser o mesmo");
        assertEquals(appointment.getNotes(), result.getNotes(), "As observacoes devem ser as mesmas");

        // 12. Assert: Validar se os ids dos relacionamentos salvos foram preservados
        assertEquals(savedCustomer.getId(), result.getCustomer().getId(), "O cliente deve ser o mesmo");
        assertEquals(savedProfessional.getId(), result.getProfessional().getId(), "O profissional deve ser o mesmo");
        assertEquals(savedServiceOffering.getId(), result.getServiceOffering().getId(), "O servico deve ser o mesmo");
    }
}
