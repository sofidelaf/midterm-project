package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Accounts.Account;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    List<Account> findByPrimaryOwner(AccountHolder accountHolder);

    List<Account> findBySecondaryOwner(AccountHolder accountHolder);
}