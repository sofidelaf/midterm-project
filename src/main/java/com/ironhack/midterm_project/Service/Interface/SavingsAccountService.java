package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoSavingsAccount;
import com.ironhack.midterm_project.Model.Accounts.SavingsAccount;
import com.ironhack.midterm_project.Model.Embedded.Money;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsAccountService {
    SavingsAccount createSavingsAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey, Money minimumBalance, BigDecimal interestRate);
    List<DTOGetInfoSavingsAccount> getPersonalAccountInformation(String username);

}
