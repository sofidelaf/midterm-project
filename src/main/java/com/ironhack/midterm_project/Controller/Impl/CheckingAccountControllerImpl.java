package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateCheckingAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCheckingAccount;
import com.ironhack.midterm_project.Controller.Interface.CheckingAccountController;
import com.ironhack.midterm_project.Model.Accounts.CheckingAccount;
import com.ironhack.midterm_project.Repository.CheckingAccountRepository;
import com.ironhack.midterm_project.Service.Impl.CheckingAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CheckingAccountControllerImpl implements CheckingAccountController {

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private CheckingAccountServiceImpl checkingAccountService;

    @GetMapping("/accounts/checking_account")
    @ResponseStatus(HttpStatus.OK)
    public List<CheckingAccount> getAll() {
        return checkingAccountRepository.findAll();
    }

    @PostMapping("/accounts/checking_account")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createAccount(@RequestBody DTOCreateCheckingAccount dtoCreateCheckingAccount) {
        try{
            return checkingAccountService.createCheckingOrStudentAccount(
                    dtoCreateCheckingAccount.getPrimaryOwnerUserId(),
                    dtoCreateCheckingAccount.getSecondaryOwnerUserId(),
                    dtoCreateCheckingAccount.getInitialBalance(),
                    dtoCreateCheckingAccount.getSecretKey()
            );
        } catch(
                InvalidDataAccessApiUsageException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Element must not be null");

        }

    }


    @GetMapping("/accounts/checking_accounts/my_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<DTOGetInfoCheckingAccount> getPersonalAccountInformation(Authentication authentication) {
        String username = authentication.getName();

        return checkingAccountService.getPersonalAccountInformation(username);
    }

}

