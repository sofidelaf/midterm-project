package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoStudentCheckingAccount;
import com.ironhack.midterm_project.Controller.Interface.StudentCheckingAccountController;
import com.ironhack.midterm_project.Model.Accounts.StudentCheckingAccount;
import com.ironhack.midterm_project.Repository.StudentCheckingAccountRepository;
import com.ironhack.midterm_project.Service.Interface.StudentCheckingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentCheckingAccountControllerImpl implements StudentCheckingAccountController {

    @Autowired
    private StudentCheckingAccountRepository studentCheckingAccountRepository;

    @Autowired
    private StudentCheckingAccountService studentCheckingAccountService;

    @GetMapping("/accounts/student_checking_account")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentCheckingAccount> getAll() {
        return studentCheckingAccountRepository.findAll();
    }



    @GetMapping("/accounts/student_checking_accounts/my_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<DTOGetInfoStudentCheckingAccount> getPersonalAccountInformation(Authentication authentication) {
        String username = authentication.getName();

        return studentCheckingAccountService.getPersonalStudentAccountInformation(username);
    }
}

