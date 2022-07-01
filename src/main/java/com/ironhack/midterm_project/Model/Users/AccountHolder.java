package com.ironhack.midterm_project.Model.Users;


import com.ironhack.midterm_project.Model.Embedded.Address;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
public class AccountHolder extends User{

    @NotNull
    private LocalDate dateOfBirth;

    @Embedded
    @NotNull
    private Address primaryAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "mailing_street_address")),
            @AttributeOverride(name = "city", column = @Column(name = "mailing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "mailing_state")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "mailing_zipCode"))
    })
    private Address mailingAddress;

    // Constructor methods
    public AccountHolder() {
    }

    public AccountHolder(String username, String password, String name, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
        super(username, password, name);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
    }

    public AccountHolder(String username, String password, String name, LocalDate dateOfBirth, Address primaryAddress) {
        super(username, password, name);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
    }

    // Getters and setters

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountHolder that = (AccountHolder) o;
        return Objects.equals(dateOfBirth, that.dateOfBirth) && Objects.equals(primaryAddress, that.primaryAddress) && Objects.equals(mailingAddress, that.mailingAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateOfBirth, primaryAddress, mailingAddress);
    }
}

