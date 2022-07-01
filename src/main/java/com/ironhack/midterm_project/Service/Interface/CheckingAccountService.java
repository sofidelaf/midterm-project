package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCheckingAccount;
import com.ironhack.midterm_project.Model.Accounts.StudentCheckingAccount;
import com.ironhack.midterm_project.Model.Embedded.Money;

import java.util.List;

public interface CheckingAccountService {
    Object createCheckingOrStudentAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey);
    List<DTOGetInfoCheckingAccount> getPersonalAccountInformation(String username);
}
