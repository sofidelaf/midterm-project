package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Model.Accounts.Account;
import com.ironhack.midterm_project.Model.Accounts.Transaction;

public interface TransactionService {
    boolean multipleTransactionFraud(Transaction transaction, Account account);
}
