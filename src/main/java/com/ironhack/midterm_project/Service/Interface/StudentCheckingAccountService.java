package com.ironhack.midterm_project.Service.Interface;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoStudentCheckingAccount;

import java.util.List;

public interface StudentCheckingAccountService {
    List<DTOGetInfoStudentCheckingAccount> getPersonalStudentAccountInformation(String username);
}
