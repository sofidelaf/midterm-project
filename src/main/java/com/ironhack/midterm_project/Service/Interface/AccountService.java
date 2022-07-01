package com.ironhack.midterm_project.Service.Interface;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    void updateAccount(Long id, String username, String actionType, BigDecimal amount);

    void transfer(Long ownAccountId, String username, BigDecimal amount, Long recipientAccountId, String recipientName);

    void thirdPartyTransfer(String username ,String hashedKey,String actionType,BigDecimal amount, Long recipientAccountId, String recipientAccountSecretKey);

    List<Object> getAllUsersAccounts(String username);
}
