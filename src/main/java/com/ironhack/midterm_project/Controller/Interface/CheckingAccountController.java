package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateCheckingAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCheckingAccount;
import com.ironhack.midterm_project.Model.Accounts.CheckingAccount;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CheckingAccountController {
    List<CheckingAccount> getAll();
    Object createAccount(DTOCreateCheckingAccount dtoCreateCheckingAccount);
    List<DTOGetInfoCheckingAccount> getPersonalAccountInformation(Authentication authentication);
}
