package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCreditCardsAccount;
import com.ironhack.midterm_project.Model.Accounts.CreditCardAccount;
import com.ironhack.midterm_project.Model.Embedded.Money;

import java.math.BigDecimal;
import java.util.List;

public interface CreditCardAccountService {
    CreditCardAccount createCreditCardAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, String secretKey, BigDecimal interestRate, Money creditLimit);
    List<DTOGetInfoCreditCardsAccount> getPersonalAccountInformation(String username);
}
