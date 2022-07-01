package com.ironhack.midterm_project.Model.Accounts;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name= "account_id")
public class StudentCheckingAccount extends Account{

    public StudentCheckingAccount() {
    }
}
