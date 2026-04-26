package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "appointments")
public class Appointment {

    private static final long NO_SHOW_GRACE_MINUTES = 15L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_offering_id", nullable = false)
    private ServiceOffering serviceOffering;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AppointmentStatus status;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Appointment() {
    }

    public Appointment(Long id, Customer customer, Professional professional, ServiceOffering serviceOffering,
                       LocalDateTime scheduledAt, AppointmentStatus status, String notes, String cancelReason) {
        this.id = id;
        this.customer = customer;
        this.professional = professional;
        this.serviceOffering = serviceOffering;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.notes = notes;
        this.cancelReason = cancelReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public ServiceOffering getServiceOffering() {
        return serviceOffering;
    }

    public void setServiceOffering(ServiceOffering serviceOffering) {
        this.serviceOffering = serviceOffering;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    // Domain behavior to implement when appointment rules are modeled.
    public void confirm() {
        if (this.status == AppointmentStatus.CONFIRMED) {
            throw new DomainException("Agendamento ja esta confirmado.");
        }
        if (this.status != AppointmentStatus.SCHEDULED) {
            throw new DomainException("So e possivel confirmar agendamentos pendentes.");
        }

        this.status = AppointmentStatus.CONFIRMED;

    }


    public void cancel(String reason) {
        if (this.status == AppointmentStatus.CANCELED) {
            throw new DomainException("Agendamento ja esta cancelado.");
        }
        if (this.status == AppointmentStatus.DONE) {
            throw new DomainException("Nao e possivel cancelar um atendimento concluido.");
        }
        if (this.status == AppointmentStatus.NO_SHOW) {
            throw new DomainException("Nao e possivel cancelar: o cliente nao compareceu.");
        }

        this.status = AppointmentStatus.CANCELED;
        this.cancelReason = reason;
    }

    public void markAsDone() {
        if (this.status == AppointmentStatus.DONE) {
            throw new DomainException("Este atendimento ja foi concluido.");
        }
        if (this.status == AppointmentStatus.CANCELED) {
            throw new DomainException("Nao e possivel concluir um agendamento cancelado.");
        }
        if (this.status != AppointmentStatus.CONFIRMED) {
            throw new DomainException("So e possivel concluir agendamentos confirmados.");
        }

        if (LocalDateTime.now().isBefore(scheduledAt)) {
            throw new DomainException("Nao e possivel concluir um agendamento antes do horario previsto.");
        }

        this.status = AppointmentStatus.DONE;
    }

    public void markAsNoShow() {
        if (this.status == AppointmentStatus.CANCELED) {
            throw new DomainException("Agendamento ja esta cancelado.");
        }
        if (this.status == AppointmentStatus.DONE) {
            throw new DomainException("Nao e possivel marcar no-show para um atendimento ja concluido.");
        }
        if (this.status == AppointmentStatus.NO_SHOW) {
            throw new DomainException("Este agendamento ja esta marcado como no-show.");
        }

        // TODO: Keep this threshold configurable when application settings exist.
        if (LocalDateTime.now().isBefore(scheduledAt.plusMinutes(NO_SHOW_GRACE_MINUTES))) {
            throw new DomainException("Ainda nao e possivel marcar falta antes da janela configurada.");
        }

        this.status = AppointmentStatus.NO_SHOW;
    }

    public void reschedule(LocalDateTime newScheduledAt) {
        if (this.status == AppointmentStatus.DONE) {
            throw new DomainException("Nao e possivel reagendar um atendimento ja concluido.");
        }
        if (this.status == AppointmentStatus.CANCELED) {
            throw new DomainException("Agendamento ja esta cancelado, faca um novo agendamento.");
        }
        if (this.status == AppointmentStatus.NO_SHOW) {
            throw new DomainException("Este agendamento ja esta marcado como no-show, faca um novo agendamento.");
        }
        if (newScheduledAt.isBefore(LocalDateTime.now())) {
            throw new DomainException("Nao e possivel reagendar para uma data que ja passou.");
        }

        this.scheduledAt = newScheduledAt;
        this.status = AppointmentStatus.SCHEDULED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appointment that = (Appointment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
