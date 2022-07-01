package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Model.Embedded.Address;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Model.Users.Role;

import java.time.LocalDate;
import java.util.Set;

public interface AccountHolderService {
    AccountHolder createAccountHolder (String username, String password, String name, Set<Role> roles, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress);
}
