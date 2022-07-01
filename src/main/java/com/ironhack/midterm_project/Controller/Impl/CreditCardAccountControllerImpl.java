package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOCreateCreditCardAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCreditCardsAccount;
import com.ironhack.midterm_project.Controller.Interface.CreditCardAccountController;
import com.ironhack.midterm_project.Model.Accounts.CreditCardAccount;
import com.ironhack.midterm_project.Repository.CreditCardAccountRepository;
import com.ironhack.midterm_project.Service.Impl.CreditCardAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CreditCardAccountControllerImpl implements CreditCardAccountController {

    @Autowired
    private CreditCardAccountRepository creditCardAccountRepository;

    @Autowired
    private CreditCardAccountServiceImpl creditCardAccountService;


    @GetMapping("/accounts/credit_card_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCardAccount> getAll() {
        return creditCardAccountRepository.findAll();
    }

    @PostMapping("/accounts/credit_card_accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardAccount createCreditCardAccount(@RequestBody DTOCreateCreditCardAccount dtoCreateCreditCardAccount) {
        try {
            return creditCardAccountService.createCreditCardAccount(
                    dtoCreateCreditCardAccount.getPrimaryOwnerUserId(),
                    dtoCreateCreditCardAccount.getSecondaryOwnerUserId(),
                    dtoCreateCreditCardAccount.getSecretKey(),
                    dtoCreateCreditCardAccount.getInterestRate(),
                    dtoCreateCreditCardAccount.getCreditLimit()
            );

        } catch(InvalidDataAccessApiUsageException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Element must not be null");
        }
    }

    @GetMapping("/accounts/credit_card_accounts/my_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<DTOGetInfoCreditCardsAccount> getPersonalAccountInformation(Authentication authentication) {
        String user = authentication.getName();
        return creditCardAccountService.getPersonalAccountInformation(user);
    }
}

