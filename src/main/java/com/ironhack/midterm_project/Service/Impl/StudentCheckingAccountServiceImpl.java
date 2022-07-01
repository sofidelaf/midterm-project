package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoStudentCheckingAccount;
import com.ironhack.midterm_project.Model.Accounts.StudentCheckingAccount;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.StudentCheckingAccountRepository;
import com.ironhack.midterm_project.Service.Interface.StudentCheckingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentCheckingAccountServiceImpl implements StudentCheckingAccountService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private StudentCheckingAccountRepository studentCheckingAccountRepository;


    public List<DTOGetInfoStudentCheckingAccount> getPersonalStudentAccountInformation(String username) {

        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Account Holder with username: " + username));

        List<DTOGetInfoStudentCheckingAccount> infoStudentCheckingAccountList = new ArrayList<>();

        for(StudentCheckingAccount studentCheckingAccount : studentCheckingAccountRepository.findByPrimaryOwner(accountHolder)){

            String secondaryOwnerName = studentCheckingAccount.getSecondaryOwner() != null ? studentCheckingAccount.getSecondaryOwner().getName() : null;

            DTOGetInfoStudentCheckingAccount newDTO = new DTOGetInfoStudentCheckingAccount(
                    studentCheckingAccount.getId(),
                    studentCheckingAccount.getBalance(),
                    studentCheckingAccount.getPrimaryOwner().getName(),
                    secondaryOwnerName,
                    studentCheckingAccount.getSecretKey()
            );
            infoStudentCheckingAccountList.add(newDTO);
        }

        for(StudentCheckingAccount studentCheckingAccount: studentCheckingAccountRepository.findBySecondaryOwner(accountHolder)){


            DTOGetInfoStudentCheckingAccount newDTO = new DTOGetInfoStudentCheckingAccount(
                    studentCheckingAccount.getId(),
                    studentCheckingAccount.getBalance(),
                    studentCheckingAccount.getPrimaryOwner().getName(),
                    studentCheckingAccount.getSecondaryOwner().getName(),
                    studentCheckingAccount.getSecretKey()
            );
            infoStudentCheckingAccountList.add(newDTO);
        }

        return infoStudentCheckingAccountList;

    }
}

