package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateAccountHolder;
import com.ironhack.midterm_project.Controller.Interface.AccountHolderController;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Service.Interface.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountHolderControllerImpl implements AccountHolderController {
    @Autowired
    private AccountHolderService accountHolderService;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @PostMapping("/users/account_holders")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder createAccountHolder(@RequestBody DTOCreateAccountHolder dtoCreateAccountHolder) {
        return accountHolderService.createAccountHolder(
                dtoCreateAccountHolder.getUsername(),
                dtoCreateAccountHolder.getPassword(),
                dtoCreateAccountHolder.getName(),
                dtoCreateAccountHolder.getRoles(),
                dtoCreateAccountHolder.getDateOfBirth(),
                dtoCreateAccountHolder.getPrimaryAddress(),
                dtoCreateAccountHolder.getMailingAddress()
        );
    }

    @GetMapping("/users/account_holders")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountHolder> getAll(){
        return accountHolderRepository.findAll();
    }
}

