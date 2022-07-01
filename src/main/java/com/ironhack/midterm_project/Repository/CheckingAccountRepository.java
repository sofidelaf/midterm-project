package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Accounts.CheckingAccount;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckingAccountRepository extends JpaRepository<CheckingAccount,Long> {

    List<CheckingAccount> findByPrimaryOwner(AccountHolder accountHolder);

    List<CheckingAccount> findBySecondaryOwner(AccountHolder accountHolder);
}