package com.alberdev.study.appointmenthub.domain.entities;

import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 25, nullable = false, unique = true)
    private String phone;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "customer")
    private Set<Appointment> appointments = new HashSet<>();

    public Customer() {
    }

    public Customer(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.active = true;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }


    public void addAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new DomainException("O agendamento nao pode ser nulo.");
        }
        if (!this.isActive()) {
            throw new DomainException("Nao e possivel agendar para um cliente inativo.");
        }

        appointment.setCustomer(this);
        this.appointments.add(appointment);
    }

    public void activate() {
        if (active) {
            throw new DomainException("Cliente ja esta ativo.");
        }

        active = true;
    }

    public void deactivate() {
        if (!active) {
            throw new DomainException("Cliente ja esta inativo.");
        }

        active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
