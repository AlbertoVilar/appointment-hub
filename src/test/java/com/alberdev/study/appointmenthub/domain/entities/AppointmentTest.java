package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AppointmentTest {

    private Appointment appointment; // Objeto real que estamos testando 🎯

    @Mock
    private Customer customer;

    @Mock
    private Professional professional;

    @Mock
    private ServiceOffering serviceOffering;

    @BeforeEach
    void setUp() {
        // Preparamos o cenário inicial: um agendamento com status SCHEDULED 🚩
        appointment = new Appointment(
                1L,
                customer,
                professional,
                serviceOffering,
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.SCHEDULED,
                "Nota de teste",
                null
        );
    }

    // Confirm appointment

    @Test
    void shouldConfirmAppointmentSuccessfully() {
        // 1. Arrange: O objeto já foi criado no setUp com status SCHEDULED
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());

        // 2. Act: Executamos a ação de confirmar
        appointment.confirm();

        // 3. Assert: Verificamos se o status mudou para CONFIRMED ✅
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenConfirmingCanceledAppointment() {
        // 1. Arrange: Forçamos o estado inicial para CANCELED 🚩
        appointment.setStatus(AppointmentStatus.CANCELED);

        // 2. Act & Assert: Capturamos a exceção lançada 😱
        assertThrows(DomainException.class, () -> {
            appointment.confirm();
        });

        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
    }

    // Cancel appointment

    @Test
    void shouldCancelWithReasonSuccessfully() {
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        appointment.cancel("Cliente desistiu");

        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
        assertEquals("Cliente desistiu", appointment.getCancelReason());
    }

    @Test
    void shouldCancelWithoutReasonSuccess() {
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        appointment.cancel();
        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
        assertEquals("", appointment.getCancelReason());
    }

    @Test
    void shouldCancelWhenStatusIsConfirmed() {
        // 1. Arrange: Agendamento confirmado
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        // 2. Act
        appointment.cancel("Cliente solicitou via telefone");

        // 3. Assert
        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancelReasonIsNull() {
        // 2. Act & Assert: Motivo nulo deve ser proibido
        assertThrows(DomainException.class, () -> {
            appointment.cancel(null);
        });

        // 3. Assert: Mantém o status original (SCHEDULED vindo do setUp)
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenAlreadyCanceled() {
        // 1. Arrange
        appointment.cancel("Primeiro cancelamento");

        // 2. Act & Assert: Não pode cancelar o que já está cancelado
        assertThrows(DomainException.class, () -> {
            appointment.cancel("Tentando cancelar de novo");
        });
    }

    @Test
    void shouldThrowExceptionWhenCancelingNoShow() {
        // 1. Arrange
        appointment.setStatus(AppointmentStatus.NO_SHOW);

        // 2. Act & Assert
        assertThrows(DomainException.class, () -> {
            appointment.cancel("Cliente faltou, não há o que cancelar");
        });

        // 3. Assert
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancelingDoneAppointment() {
        // 1. Arrange: Mudar o status para DONE antes do teste ⚙️
        appointment.setStatus(AppointmentStatus.DONE);

        // 2. Act & Assert: Capturamos a exceção lançada 😱
        assertThrows(DomainException.class, () -> {
            appointment.cancel();
        });

        assertEquals(AppointmentStatus.DONE, appointment.getStatus());

    }

    // Mark appointment as done

    @Test
    void shouldMarkAsDoneSuccessfully() {
        // 1. Arrange: Preparar o agendamento para o estado "Confirmado" e no "Passado"
        // Dica: Use os métodos setStatus() e setScheduledAt()
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setScheduledAt(LocalDateTime.now().minusDays(1));

        // 2. Act: Executar a ação
        appointment.markAsDone();

        // 3. Assert: Verificar se o status mudou para DONE
        assertEquals(AppointmentStatus.DONE, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMarkingAsDoneWithStatusScheduled() {
        // 1. Arrange: O agendamento já inicia como SCHEDULED pelo setUp().
        // Vamos garantir que o horário já passou para que o status seja o único impedimento.
        appointment.setScheduledAt(LocalDateTime.now().minusHours(1));

        // 2. Act & Assert: Validamos que o "grito" (exceção) acontece
        assertThrows(DomainException.class, () -> {
            appointment.markAsDone();
        });

        // 3. Verificação de Estado: O status NÃO deve ter mudado para DONE
        // Como você escreveria o assertEquals para provar que ele continua SCHEDULED? 🧐
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMarkingAsDoneInFutureTime() {
        // 1. Arrange: O agendamento precisa estar CONFIRMED e no FUTURO 🚀
        // [SUAS LINHAS AQUI]
        appointment.setScheduledAt(LocalDateTime.now().plusDays(5));
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        // 2. Act & Assert: Validar que o sistema impede a finalização antecipada
        assertThrows(DomainException.class, () -> {
            appointment.markAsDone();
        });

        // 3. Assert: Garantir que o status permaneceu CONFIRMED
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
    }

    // Mark appointment as no-show

    @Test
    void shouldMarkAsNoShowSuccessfully() {
        // 1. Arrange: Preparar para o sucesso do No Show
        // Precisamos de: status CONFIRMED + tempo maior que 15 minutos no passado.
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setScheduledAt(LocalDateTime.now().minusMinutes(20)); // 20 min atrás

        // 2. Act
        appointment.markAsNoShow();

        // 3. Assert
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMarkingNoShowWithinTolerance() {
        // 1. Arrange: Status CONFIRMED e horário apenas 5 minutos atrás (dentro dos 15) ⏱️
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setScheduledAt(LocalDateTime.now().minusMinutes(5));

        // 2. Act & Assert: Validar que o sistema impede a falta antes da tolerância
        assertThrows(DomainException.class, () -> {
            appointment.markAsNoShow();
        });

        // 3. Assert: O que deve acontecer com o status aqui? 🧐
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMarkingNoShowWithStatusScheduled() {
        // 1. Arrange: Garantir que o tempo já passou, mas o status é SCHEDULED 📅
        appointment.setScheduledAt(LocalDateTime.now().minusMinutes(30));
        // O status já é SCHEDULED por padrão do setUp()

        // 2. Act & Assert: Tentar marcar No Show deve falhar 🚫
        assertThrows(DomainException.class, () -> {
            appointment.markAsNoShow();
        });

        // 3. Assert: Verificar se o status permanece SCHEDULED
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    // Reschedule appointment

    @Test
    void shouldRescheduleWhenStatusIsScheduled() {
        // 1. Arrange: Definir uma nova data para o futuro 🗓️
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);

        // 2. Act: Chamar o método de reagendamento
        // [SUA LINHA AQUI]
        appointment.reschedule(newDate);

        // 3. Assert: Verificar se a data mudou e o status continua SCHEDULED ✅
        assertEquals(newDate, appointment.getScheduledAt());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void shouldRescheduleAndResetStatusWhenStatusIsConfirmed() {
        // 1. Arrange: Colocar o agendamento como CONFIRMED
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        LocalDateTime newDate = LocalDateTime.now().plusDays(3);

        // 2. Act: Reagendar para a nova data
        appointment.reschedule(newDate);

        // 3. Assert: Validar a nova data E se o status VOLTOU para SCHEDULED 🔙
        assertEquals(newDate, appointment.getScheduledAt());
        // Qual seria a linha para verificar que o status agora é SCHEDULED?
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenReschedulingToPastDate() {
        // 1. Arrange: Guardamos a data original e criamos uma data no passado
        LocalDateTime originalDate = appointment.getScheduledAt();
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // 2. Act & Assert: Tentamos o reagendamento inválido
        assertThrows(DomainException.class, () -> {
            appointment.reschedule(pastDate);
        });

        // 3. Assert: Garantir que a data do agendamento NÃO mudou
        assertEquals(originalDate, appointment.getScheduledAt());
    }

    @Test
    void shouldThrowExceptionWhenReschedulingCanceledAppointment() {
        // 1. Arrange: Colocar o agendamento no status CANCELED ❌
        // [SUA LINHA DE CÓDIGO AQUI]
        appointment.setStatus(AppointmentStatus.CANCELED);
        // 2. Act & Assert: Tentar reagendar para uma data válida no futuro
        assertThrows(DomainException.class, () -> {
            appointment.reschedule(LocalDateTime.now().plusDays(1));
        });

        // 3. Assert: Garantir que o status permaneceu CANCELED
        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenReschedulingDoneAppointment() {
        // 1. Arrange: Colocar o agendamento no status DONE ✅
        // Dica: Use o método que você já conhece para mudar o status
        appointment.setStatus(AppointmentStatus.DONE);

        // 2. Act & Assert: Tentar reagendar para o futuro
        assertThrows(DomainException.class, () -> {
            appointment.reschedule(LocalDateTime.now().plusDays(1));
        });

        // 3. Assert: Garantir que o status não "escapou" de DONE
        assertEquals(AppointmentStatus.DONE, appointment.getStatus());
    }
    @Test
    void shouldThrowExceptionWhenReschedulingNoShowAppointment() {
        // 1. Arrange: Agendamento marcado como falta 👤❌
        appointment.setStatus(AppointmentStatus.NO_SHOW);

        // 2. Act & Assert: Tentativa de reagendar deve lançar exceção
        assertThrows(DomainException.class, () -> {
            appointment.reschedule(LocalDateTime.now().plusDays(1));
        });

        // 3. Assert: O status deve permanecer NO_SHOW
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());
    }
}
