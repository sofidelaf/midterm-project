package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateCreditCardAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCreditCardsAccount;
import com.ironhack.midterm_project.Model.Accounts.CreditCardAccount;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CreditCardAccountController {
    List<CreditCardAccount> getAll();
    CreditCardAccount createCreditCardAccount(DTOCreateCreditCardAccount dtoCreateCreditCardAccount);
    List<DTOGetInfoCreditCardsAccount> getPersonalAccountInformation(Authentication authentication);
}