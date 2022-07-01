package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Accounts.StudentCheckingAccount;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCheckingAccountRepository extends JpaRepository<StudentCheckingAccount,Long> {


    List<StudentCheckingAccount> findByPrimaryOwner(AccountHolder accountHolder);

    List<StudentCheckingAccount> findBySecondaryOwner(AccountHolder accountHolder);
}