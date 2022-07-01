package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateAccountHolder;
import com.ironhack.midterm_project.Model.Users.AccountHolder;

import java.util.List;

public interface AccountHolderController {
    List<AccountHolder> getAll();
    AccountHolder createAccountHolder (DTOCreateAccountHolder dtoCreateAccountHolder);
}