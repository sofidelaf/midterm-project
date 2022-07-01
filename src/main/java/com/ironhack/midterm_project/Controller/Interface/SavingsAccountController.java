package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateSavingsAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoSavingsAccount;
import com.ironhack.midterm_project.Model.Accounts.SavingsAccount;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface SavingsAccountController {
    List<SavingsAccount> getAll();
    SavingsAccount createAccount(DTOCreateSavingsAccount dtoCreateSavingsAccount);
    List<DTOGetInfoSavingsAccount> getPersonalAccountInformation(Authentication authentication);
}