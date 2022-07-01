package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Accounts.SavingsAccount;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount,Long> {

    List<SavingsAccount> findByPrimaryOwner (AccountHolder primaryOwner);

    List<SavingsAccount> findBySecondaryOwner (AccountHolder primaryOwner);

}