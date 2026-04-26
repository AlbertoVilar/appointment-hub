package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.enums.AppointmentStatus;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "appointments")
public class Appointment {

    private static final long NO_SHOW_GRACE_MINUTES = 15L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

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

    // Construtores
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

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Professional getProfessional() { return professional; }
    public void setProfessional(Professional professional) { this.professional = professional; }
    public ServiceOffering getServiceOffering() { return serviceOffering; }
    public void setServiceOffering(ServiceOffering serviceOffering) { this.serviceOffering = serviceOffering; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    // Comportamentos de Domínio (Regras de Negócio)
    public void confirm() {
        ensureStatusIn(AppointmentStatus.SCHEDULED);
        this.status = AppointmentStatus.CONFIRMED;
    }

    public void cancel() {
        cancel("");
    }

    public void cancel(String reason) {
        if (reason == null) {
            throw new DomainException("Motivo do cancelamento nao pode ser nulo.");
        }
        ensureStatusIn(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

        this.status = AppointmentStatus.CANCELED;

        this.cancelReason = reason;
    }

    public void markAsDone() {
        ensureStatusIn(AppointmentStatus.CONFIRMED);

        if (LocalDateTime.now().isBefore(scheduledAt)) {
            throw new DomainException("Não é possível concluir um agendamento antes do horário previsto.");
        }

        this.status = AppointmentStatus.DONE;
    }

    public void markAsNoShow() {
        ensureStatusIn(AppointmentStatus.CONFIRMED);

        if (LocalDateTime.now().isBefore(scheduledAt.plusMinutes(NO_SHOW_GRACE_MINUTES))) {
            throw new DomainException("Ainda não é possível marcar falta antes da janela de tolerância configurada.");
        }

        this.status = AppointmentStatus.NO_SHOW;
    }

    public void reschedule(LocalDateTime newScheduledAt) {
        ensureStatusIn(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        if (newScheduledAt.isBefore(LocalDateTime.now())) {
            throw new DomainException("Não é possível reagendar para uma data que já passou.");
        }

        this.scheduledAt = newScheduledAt;
        this.status = AppointmentStatus.SCHEDULED;
    }

    // Métodos Auxiliares e Guardiões
    private void ensureStatusIn(AppointmentStatus... allowedStatuses) {
        for (AppointmentStatus allowedStatus : allowedStatuses) {
            if (this.status == allowedStatus) {
                return;
            }
        }

        throw new DomainException(
                "Transição proibida: o agendamento está em " + this.status +
                        ", mas esta operação exige um destes status: " +
                        Arrays.toString(allowedStatuses) + "."
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
