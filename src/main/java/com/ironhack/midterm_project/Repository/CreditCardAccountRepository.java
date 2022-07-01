package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Accounts.CreditCardAccount;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardAccountRepository extends JpaRepository<CreditCardAccount,Long> {

    List<CreditCardAccount> findByPrimaryOwner(AccountHolder accountHolder);

    List<CreditCardAccount> findBySecondaryOwner(AccountHolder accountHolder);
}