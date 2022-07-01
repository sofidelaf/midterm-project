package com.ironhack.midterm_project.Controller.Impl;

import com.ironhack.midterm_project.Controller.Interface.AccountController;
import com.ironhack.midterm_project.Model.Accounts.Account;
import com.ironhack.midterm_project.Model.Embedded.Money;
import com.ironhack.midterm_project.Repository.AccountRepository;
import com.ironhack.midterm_project.Service.Impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
public class AccountControllerImpl implements AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountServiceImpl accountService;

    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @PatchMapping("/accounts/update_balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBalance(@PathVariable(name = "id") Long id, @RequestParam(name = "balance") BigDecimal new_balance) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no account in the Database with id: " + id));
        account.setBalance(new Money(new_balance,account.getBalance().getCurrency()));
        accountRepository.save(account);
    }

    @PatchMapping("/accounts/my_account/update_balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwnBalance(@PathVariable(name = "id") Long id,@RequestParam(name = "action_type") String actionType,@RequestParam BigDecimal amount,Authentication authentication) {
        String user = authentication.getName();

        accountService.updateAccount(id,user,actionType,amount.setScale(2, RoundingMode.HALF_EVEN));

    }

    @PatchMapping("/accounts/my_account/transfer/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@PathVariable(name = "id") Long ownAccountId,@RequestParam BigDecimal amount,@RequestParam(name = "recipient_name") String recipientName,@RequestParam(name = "recipient_account_id") Long recipientAccountId, Authentication authentication) {
        String user = authentication.getName();
        accountService.transfer(
                ownAccountId,
                user,
                amount,
                recipientAccountId,
                recipientName
        );
    }


    @PatchMapping("/accounts/third_party_transfer/{hashed_key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void thirdPartyTransfer(@PathVariable(name = "hashed_key") String hashedKey,
                                   @RequestParam(name = "action_type") String actionType,
                                   @RequestParam BigDecimal amount,
                                   @RequestParam(name = "recipient_account_id") Long recipientAccountId,
                                   @RequestParam(name = "secret_key") String recipientAccountSecretKey,
                                   Authentication authentication) {

        String username = authentication.getName();

        accountService.thirdPartyTransfer(
                username,
                hashedKey,
                actionType,
                amount,
                recipientAccountId,
                recipientAccountSecretKey
        );
    }


    @GetMapping("/accounts/my_accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Object> getPersonalAccountInformation(Authentication authentication) {
        String username = authentication.getName();

        return accountService.getAllUsersAccounts(username);
    }

    @GetMapping("/accounts/by_id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account getAccountInformation(@PathVariable(name = "id") Long id) {
        Account account = accountRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The account id: " + id + " does not exist in the Database"));

        return account;
    }


}