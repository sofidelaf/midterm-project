package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Model.Accounts.Account;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface AccountController {
    List<Account> getAll();
    void updateBalance(Long id, BigDecimal new_balance);
    void updateOwnBalance(Long id, String actionType, BigDecimal amount, Authentication authentication);
    void transfer(Long ownAccountId,BigDecimal amount,String recipientName,Long recipientAccountId, Authentication authentication );
    void thirdPartyTransfer(String hashedKey,String actionType,BigDecimal amount, Long recipientAccountId, String recipientAccountSecretKey, Authentication authentication);
    List<Object> getPersonalAccountInformation(Authentication authentication);
    Account getAccountInformation(Long id);
}
