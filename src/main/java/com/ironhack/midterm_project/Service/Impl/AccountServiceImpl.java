package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCheckingAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCreditCardsAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoSavingsAccount;
import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoStudentCheckingAccount;
import com.ironhack.midterm_project.Controller.Interface.Enum.Status;
import com.ironhack.midterm_project.Model.Accounts.Account;
import com.ironhack.midterm_project.Model.Accounts.Transaction;
import com.ironhack.midterm_project.Model.Embedded.Money;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Model.Users.ThirdPartyUser;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.AccountRepository;
import com.ironhack.midterm_project.Repository.ThirdPartyUserRepository;
import com.ironhack.midterm_project.Repository.TransactionRepository;
import com.ironhack.midterm_project.Service.Interface.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ThirdPartyUserRepository thirdPartyUserRepository;

    @Autowired
    private CheckingAccountServiceImpl checkingAccountService;

    @Autowired
    private CreditCardAccountServiceImpl creditCardAccountService;

    @Autowired
    private SavingsAccountServiceImpl savingsAccountService;

    @Autowired
    private StudentCheckingAccountServiceImpl studentCheckingAccountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionServiceImpl transactionService;

    private final String INGRESS = "ingress";
    private final String EXTRACT = "extract";
    private final String SEND = "send";
    private final String RECEIVE = "receive";

    public void updateAccount(Long id, String username, String actionType, BigDecimal amount) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no account in the Database with id: " + id));
        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no Account Holder in the Database with username: " + username));
        BigDecimal newBalanceAmount = account.getBalance().getAmount();
        Currency currency = account.getBalance().getCurrency();

        // Own account validation

        if (!account.getPrimaryOwner().equals(accountHolder)) {
            if (account.getSecondaryOwner() == null || !account.getSecondaryOwner().equals(accountHolder)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The user: " + username + " cannot access this ownAccount");
            }
        }

        // Data validation

        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than zero");
        }

        // Execution

        if (INGRESS.equalsIgnoreCase(actionType)) {
            newBalanceAmount = newBalanceAmount.add(amount);
            account.setBalance(new Money(newBalanceAmount, currency));
            accountRepository.save(account);
        } else if (EXTRACT.equalsIgnoreCase(actionType)) {
            newBalanceAmount = newBalanceAmount.subtract(amount);
            account.setBalance(new Money(newBalanceAmount, currency));
            accountRepository.save(account);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Action parameter not available. Available actions are " + INGRESS + " & " + EXTRACT);
        }
    }

    public void transfer(Long ownAccountId, String username, BigDecimal amount, Long recipientAccountId, String recipientName) {
        Account ownAccount = accountRepository.findById(ownAccountId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no ownAccount in the Database with id: " + ownAccountId));
        AccountHolder ownAccountHolder = accountHolderRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no Account Holder in the Database with username: " + username));
        Account recipientAccount = accountRepository.findById(recipientAccountId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no recipientAccount in the Database with id: " + recipientAccountId));
        String recipientPrimaryOwnerName = recipientAccount.getPrimaryOwner().getName().trim();
        String recipientSecondaryOwnerName = null;
        if(recipientAccount.getSecondaryOwner() != null){
            recipientSecondaryOwnerName = recipientAccount.getSecondaryOwner().getName().trim();
        }

        // Own account validation

        if(!ownAccount.getPrimaryOwner().equals(ownAccountHolder)){
            if(ownAccount.getSecondaryOwner() == null || !ownAccount.getSecondaryOwner().equals(ownAccountHolder)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The user: "+ username + " cannot access this ownAccount");
            }
        }

        // Data validation

        if(amount.compareTo(new BigDecimal(0)) <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than zero");
        }

        // Recipient account validation

        if(!recipientPrimaryOwnerName.equalsIgnoreCase(recipientName)){
            if(recipientAccount.getSecondaryOwner() == null || !recipientSecondaryOwnerName.equalsIgnoreCase(recipientName)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recipientName + " does not match with recipient account owners");
            }
        }

        // Account Status Validation

        if(ownAccount.getStatus().equals(Status.FROZEN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ownAccount.getId() + " is frozen due to fraud detection" );
        } else if (recipientAccount.getStatus().equals(Status.FROZEN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, recipientAccount.getId() + " is frozen due to fraud detection" );
        }

        // Execution

        BigDecimal ownBalanceAmount = ownAccount.getBalance().getAmount();
        Currency ownBalanceCurrency = ownAccount.getBalance().getCurrency();
        BigDecimal recipientBalanceAmount = recipientAccount.getBalance().getAmount();
        Currency recipientBalanceCurrency = recipientAccount.getBalance().getCurrency();

        if(amount.compareTo(ownBalanceAmount)>0){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The account does not have sufficient funds for this transfer");
        } else {
            // Own account new Balance
            ownBalanceAmount = ownBalanceAmount.subtract(amount);
            ownAccount.setBalance(new Money(ownBalanceAmount,ownBalanceCurrency));

            // Recipient account new Balance
            recipientBalanceAmount = recipientBalanceAmount.add(amount);
            recipientAccount.setBalance(new Money(recipientBalanceAmount,recipientBalanceCurrency));
            accountRepository.saveAll(List.of(ownAccount,recipientAccount));

            // Storage of Transaction Record
            Transaction transaction = new Transaction(
                    ownAccount,
                    ownAccountHolder.getName(),
                    recipientAccount,
                    recipientName,
                    new Money(amount)
            );
            transactionRepository.save(transaction);

            // Fraud check and Update Status
            // Sender account
            boolean senderConsecutiveTransactionFraud = transactionService.multipleTransactionFraud(transaction,ownAccount);
            boolean senderMaximumNumberOFTransactionsFraud = transactionService.dailyQuantityOfTransactionsFraud(transaction,ownAccount);
            if(senderConsecutiveTransactionFraud || senderMaximumNumberOFTransactionsFraud){
                ownAccount.setStatus(Status.FROZEN);
                accountRepository.save(ownAccount);
            }
            // Recipient account
            boolean recipientConsecutiveTransactionFraud = transactionService.multipleTransactionFraud(transaction,recipientAccount);
            boolean recipientMaximumNumberOfTransactionsFraud = transactionService.dailyQuantityOfTransactionsFraud(transaction,recipientAccount);
            if(recipientConsecutiveTransactionFraud || recipientMaximumNumberOfTransactionsFraud){
                recipientAccount.setStatus(Status.FROZEN);
                accountRepository.save(recipientAccount);
            }
        }
    }

    public void thirdPartyTransfer(String username, String hashedKey, String actionType, BigDecimal amount, Long recipientAccountId, String recipientAccountSecretKey) {

        // Third party validation
        ThirdPartyUser thirdPartyUser = thirdPartyUserRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is no Third party user in the Database with username: " + username));

        if(!thirdPartyUser.getHashedKey().equals(hashedKey)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Third party user credentials");
        }

        // Account validation
        Account account = accountRepository.findById(recipientAccountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no account in the Database with id: " + recipientAccountId));

        if(!account.getSecretKey().equals(recipientAccountSecretKey)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect secret key");
        }

        BigDecimal newBalanceAmount = account.getBalance().getAmount();
        Currency currency = account.getBalance().getCurrency();


        /** Account Status Validation **/
        if(account.getStatus().equals(Status.FROZEN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, account.getId() + " is frozen due to fraud detection" );
        }

        /** Data validation **/
        if (amount.compareTo(new BigDecimal(0.00).setScale(2)) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than zero");
        }

        Transaction transaction;

        /** Execution - Recipient account rebalance**/
        if (SEND.equalsIgnoreCase(actionType.trim())) {
            newBalanceAmount = newBalanceAmount.add(amount);
            account.setBalance(new Money(newBalanceAmount, currency));
            accountRepository.save(account);

            /** Storage of Transaction Record**/
            transaction = new Transaction(
                    null,
                    thirdPartyUser.getName(),
                    account,
                    account.getPrimaryOwner().getName(),
                    new Money(amount)
            );
            transactionRepository.save(transaction);

        } else if (RECEIVE.equalsIgnoreCase(actionType.trim())) {
            newBalanceAmount = newBalanceAmount.subtract(amount);
            account.setBalance(new Money(newBalanceAmount, currency));
            accountRepository.save(account);

            /** Storage of Transaction Record**/
            transaction = new Transaction(
                    account,
                    account.getPrimaryOwner().getName(),
                    null,
                    thirdPartyUser.getName(),
                    new Money(amount)
            );
            transactionRepository.save(transaction);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Action parameter not available. Available actions are " + SEND + " & " + RECEIVE);
        }

        /** Fraud check **/
        //sender account
        boolean consecutiveTransactionsFraud = transactionService.multipleTransactionFraud(transaction,account);
        boolean maximumNumberOFTransactionsFraud = transactionService.dailyQuantityOfTransactionsFraud(transaction,account);

        if(consecutiveTransactionsFraud || maximumNumberOFTransactionsFraud){
            account.setStatus(Status.FROZEN);
            accountRepository.save(account);
        }


    }


    public List<Object> getAllUsersAccounts(String username) {

        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Account Holder with username: " + username));

        List<Object> infoAccountList = new ArrayList<>();

        for(DTOGetInfoSavingsAccount savingsAccount: savingsAccountService.getPersonalAccountInformation(username) ){
            infoAccountList.add(savingsAccount);
        }

        for(DTOGetInfoCheckingAccount checkingAccount: checkingAccountService.getPersonalAccountInformation(username) ){
            infoAccountList.add(checkingAccount);
        }

        for(DTOGetInfoStudentCheckingAccount studentCheckingAccount: studentCheckingAccountService.getPersonalStudentAccountInformation(username) ){
            infoAccountList.add(studentCheckingAccount);
        }

        for(DTOGetInfoCreditCardsAccount creditCardsAccount: creditCardAccountService.getPersonalAccountInformation(username) ){
            infoAccountList.add(creditCardsAccount);
        }

        return infoAccountList;


    }


    public void applicablePenaltyFee(Account account){
        BigDecimal accountBalance = account.getBalance().getAmount();
        BigDecimal penaltiFee = account.getPenaltyFee().getAmount();
        BigDecimal accountMinimumBalance;
        Boolean isPenaltyFeeApplied = account.isPenaltyFeeApplied();

        if(account.getMinimumBalance() != null){
            accountMinimumBalance = account.getMinimumBalance().getAmount();

            /** in case when check balance has drop below minimum balance**/
            if(accountBalance.compareTo(accountMinimumBalance) < 0  &&  isPenaltyFeeApplied == false){
                BigDecimal newBalanceAmount = accountBalance.subtract(penaltiFee);
                account.setBalance(new Money(newBalanceAmount,account.getBalance().getCurrency()));
                account.setPenaltyFeeApplied(true);
                accountRepository.save(account);

                /** in case when check balance has move over minimum balance**/
            } else if (accountBalance.compareTo(accountMinimumBalance) >= 0  &&  isPenaltyFeeApplied == true) {
                account.setPenaltyFeeApplied(false);
                accountRepository.save(account);

            }
        }

    }
}
