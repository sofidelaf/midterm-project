package com.ironhack.midterm_project.Controller.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoStudentCheckingAccount;
import com.ironhack.midterm_project.Model.Accounts.StudentCheckingAccount;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface StudentCheckingAccountController {
    List<StudentCheckingAccount> getAll();
    List<DTOGetInfoStudentCheckingAccount> getPersonalAccountInformation(Authentication authentication);
}
