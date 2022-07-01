package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateSavingsAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoSavingsAccount;
import com.ironhack.midterm_project.Controller.Interface.SavingsAccountController;
import com.ironhack.midterm_project.Model.Accounts.SavingsAccount;
import com.ironhack.midterm_project.Repository.SavingsAccountRepository;
import com.ironhack.midterm_project.Service.Interface.SavingsAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class SavingsAccountControllerImpl implements SavingsAccountController {

    @Autowired
    private SavingsAccountService savingsAccountService;

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;


    @GetMapping("/accounts/saving_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<SavingsAccount> getAll() {
        return savingsAccountRepository.findAll();
    }

    @PostMapping("/accounts/saving_accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccount createAccount(@RequestBody @Validated DTOCreateSavingsAccount dtoCreateSavingsAccount) {
        try{
            return savingsAccountService.createSavingsAccount(
                    dtoCreateSavingsAccount.getPrimaryOwnerUserId(),
                    dtoCreateSavingsAccount.getSecondaryOwnerUserId(),
                    dtoCreateSavingsAccount.getInitialBalance(),
                    dtoCreateSavingsAccount.getSecretKey(),
                    dtoCreateSavingsAccount.getMinimumBalance(),
                    dtoCreateSavingsAccount.getInterestRate());
        } catch(InvalidDataAccessApiUsageException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Element must not be null");
        }


    }

    @GetMapping("/accounts/saving_accounts/my_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<DTOGetInfoSavingsAccount> getPersonalAccountInformation(Authentication authentication) {
        String user = authentication.getName();
        return savingsAccountService.getPersonalAccountInformation(user);

    }
}

