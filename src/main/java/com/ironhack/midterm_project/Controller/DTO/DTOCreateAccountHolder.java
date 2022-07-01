package com.ironhack.midterm_project.Controller.DTO;

import com.ironhack.midterm_project.Model.Embedded.Address;
import com.ironhack.midterm_project.Model.Users.Role;

import java.time.LocalDate;
import java.util.Set;

public class DTOCreateAccountHolder {
    private String username;

    private String password;

    private String name;

    private Set<Role> roles;

    private LocalDate dateOfBirth;

    private Address primaryAddress;

    private Address mailingAddress;

    public DTOCreateAccountHolder(String username, String password, String name, Set<Role> roles, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.roles = roles;
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

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
}

