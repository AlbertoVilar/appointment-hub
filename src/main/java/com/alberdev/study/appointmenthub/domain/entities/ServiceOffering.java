package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "service_offerings")
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "duration_in_minutes", nullable = false)
    private Integer durationInMinutes;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "serviceOffering")
    private Set<Appointment> appointments = new HashSet<>();

    public ServiceOffering() {
    }

    public ServiceOffering(Long id, String name, Integer durationInMinutes, BigDecimal basePrice) {
        this.id = id;
        this.name = name;
        this.durationInMinutes = durationInMinutes;
        this.basePrice = basePrice;

    }

    public ServiceOffering(Long id, String name, Integer durationInMinutes, BigDecimal basePrice, boolean active) {
        this.id = id;
        this.name = name;
        this.durationInMinutes = durationInMinutes;
        this.basePrice = basePrice;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate() {

        if (active) throw new DomainException("Este servico ja esta ativo.");
        this.active = true;
    }

    public void deactivate() {
        if (!active) throw new DomainException("Este servico ja esta desativado.");
        this.active = false;
    }

    public void updateBasePrice(BigDecimal newPrice) {

        if (newPrice == null || newPrice.signum() <= 0) {
            throw new DomainException("O preco base nao pode ser nulo, zero ou negativo.");
        }

        this.basePrice = newPrice;
    }

    public void updateDuration(Integer newDurationInMinutes) {
        if (newDurationInMinutes == null || newDurationInMinutes <= 0) {
            throw new DomainException("A duracao nao pode ser nula, zero ou negativa.");
        }

        this.durationInMinutes = newDurationInMinutes;
    }

    public void addAppointment(Appointment appointment) {
        if (!active) {
            throw new DomainException("O servico precisa estar ativo.");
        }
        if (appointment == null) {
            throw new DomainException("Agendamento nao pode ser nulo.");
        }

        appointment.setServiceOffering(this);
        appointments.add(appointment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceOffering that = (ServiceOffering) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
