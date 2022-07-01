package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCreditCardsAccount;
import com.ironhack.midterm_project.Controller.Interface.Enum.Status;
import com.ironhack.midterm_project.Model.Accounts.CreditCardAccount;
import com.ironhack.midterm_project.Model.Embedded.Money;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.CreditCardAccountRepository;
import com.ironhack.midterm_project.Service.Interface.CreditCardAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class CreditCardAccountServiceImpl implements CreditCardAccountService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private CreditCardAccountRepository creditCardAccountRepository;


    public CreditCardAccount createCreditCardAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, String secretKey, BigDecimal interestRate, Money creditLimit) {

        final BigDecimal defaultInterestRate = new BigDecimal(0.2).setScale(4, RoundingMode.HALF_EVEN);
        final BigDecimal limitMinimumInterestRate = new BigDecimal(0.1);

        final BigDecimal defaultAmountCreditLimit = new BigDecimal(100);
        final Currency defaultCurrencyCreditLimit = Currency.getInstance("EUR");
        final BigDecimal maximumCreditLimitValue = new BigDecimal(100000);

        CreditCardAccount newAccount = new CreditCardAccount();

        /**User validation and instantiation**/

        AccountHolder primaryOwner = accountHolderRepository.findById(primaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+primaryOwnerUserId + " does not exist in the Database"));
        newAccount.setPrimaryOwner(primaryOwner);

        AccountHolder secondaryOwner = null;
        if(secondaryOwnerUserId != null){
            secondaryOwner = accountHolderRepository.findById(secondaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+secondaryOwnerUserId + " does not exist in the Database"));
        }
        newAccount.setSecondaryOwner(secondaryOwner);

        /**Initial Balance is set to 0**/
        newAccount.setBalance(new Money(new BigDecimal(0), Currency.getInstance("EUR")));

        /**CreationDate**/
        final LocalDateTime creationDate = LocalDateTime.now();
        newAccount.setCreationDate(creationDate);

        /**Status**/
        Status status = Status.ACTIVE;
        newAccount.setStatus(status);

        /**Secret Key**/
        if(secretKey == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Secret Key cannot be null");
        }
        newAccount.setSecretKey(secretKey);

        /**Interest Rate**/
        BigDecimal createInterestRate;

        if(interestRate == null){
            createInterestRate = defaultInterestRate;
        } else if(interestRate.compareTo(limitMinimumInterestRate) == -1){  //value provided greater than the limit returns 1
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"Interest Rate cannot be lower than the minimum value of: "+limitMinimumInterestRate);
        } else {
            createInterestRate = interestRate.setScale(4,RoundingMode.HALF_EVEN);
        }
        newAccount.setInterestRate(createInterestRate.setScale(4,RoundingMode.HALF_EVEN));

        /**Default creditLimit and definition**/
        Money createCreditLimit;



        if(creditLimit == null) {
            createCreditLimit = new Money(defaultAmountCreditLimit, defaultCurrencyCreditLimit);
        }else if (creditLimit.getAmount().compareTo(new BigDecimal(0)) == -1){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"Credit Limit cannot be lower than 0");
        } else if(creditLimit.getAmount().compareTo(maximumCreditLimitValue) == 1){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"Credit Limit cannot be greater than "+maximumCreditLimitValue.intValue());
        } else {
            createCreditLimit = creditLimit;
        }
        newAccount.setCreditLimit(createCreditLimit);

        return creditCardAccountRepository.save(newAccount);
    }


    public List<DTOGetInfoCreditCardsAccount> getPersonalAccountInformation(String username) {

        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Account Holder with username: " + username));

        List<DTOGetInfoCreditCardsAccount> infoCreditCardsAccounts = new ArrayList<>();

        for(CreditCardAccount creditCardAccount: creditCardAccountRepository.findByPrimaryOwner(accountHolder)){
            interestUpdate(creditCardAccount);

            String secondaryOwnerName = creditCardAccount.getSecondaryOwner() != null ? creditCardAccount.getSecondaryOwner().getName() : null;

            DTOGetInfoCreditCardsAccount newDTO = new DTOGetInfoCreditCardsAccount(
                    creditCardAccount.getId(),
                    creditCardAccount.getBalance(),
                    creditCardAccount.getPrimaryOwner().getName(),
                    secondaryOwnerName,
                    creditCardAccount.getSecretKey(),
                    creditCardAccount.getCreditLimit(),
                    creditCardAccount.getInterestRate()
            );
            infoCreditCardsAccounts.add(newDTO);
        }

        for(CreditCardAccount creditCardAccount: creditCardAccountRepository.findBySecondaryOwner(accountHolder)){
            interestUpdate(creditCardAccount);



            DTOGetInfoCreditCardsAccount newDTO = new DTOGetInfoCreditCardsAccount(
                    creditCardAccount.getId(),
                    creditCardAccount.getBalance(),
                    creditCardAccount.getPrimaryOwner().getName(),
                    creditCardAccount.getSecondaryOwner().getName(),
                    creditCardAccount.getSecretKey(),
                    creditCardAccount.getCreditLimit(),
                    creditCardAccount.getInterestRate()
            );
            infoCreditCardsAccounts.add(newDTO);
        }

        return infoCreditCardsAccounts;
    }

    final int INTERESTFRECUENCYMONTH = 1;
    final BigDecimal PERIODSPERYEAR = new BigDecimal(12);

    /**Variables for interest Rate **/
    private BigDecimal interestRateMonthly;
    private LocalDate today = LocalDate.now();
    private LocalDate accountCreationDate;
    private LocalDate lastInterestAddedDate;
    private long accumulatedMonthsFromCreation;
    private long accumulatedMonthsFromLastInterestUpdate;

    private BigDecimal interestRateAccrued;
    private BigDecimal amountInterestRateAccrued;

    public void interestUpdate(CreditCardAccount creditCardAccount) {

        /** Variables **/
        interestRateMonthly = creditCardAccount.getInterestRate().divide(PERIODSPERYEAR,10,RoundingMode.HALF_EVEN);

        accountCreationDate = creditCardAccount.getCreationDate().toLocalDate();
        lastInterestAddedDate = creditCardAccount.getDateLastInterestAdded();

        /** Period in months calculation **/
        accumulatedMonthsFromCreation = Period.between(accountCreationDate,today).toTotalMonths();


        /** Update from creation **/
        if(lastInterestAddedDate == null){
            if(accumulatedMonthsFromCreation >= INTERESTFRECUENCYMONTH){
                /** new balance calculation **/
                interestRateAccrued = interestRateMonthly.multiply(new BigDecimal(accumulatedMonthsFromCreation));
                amountInterestRateAccrued = interestRateAccrued.multiply(creditCardAccount.getBalance().getAmount());
                BigDecimal newBalance = creditCardAccount.getBalance().getAmount().add(amountInterestRateAccrued);

                /** Setting and keeping new balance and date of last interest calculation**/
                creditCardAccount.setBalance(new Money(newBalance,creditCardAccount.getBalance().getCurrency()));

                LocalDate interestCalculationDate = accountCreationDate.plusMonths(accumulatedMonthsFromCreation);

                creditCardAccount.setDateLastInterestAdded(interestCalculationDate);
                creditCardAccountRepository.save(creditCardAccount);
            }

        } else{ /** Update from last time**/
            accumulatedMonthsFromLastInterestUpdate = Period.between(lastInterestAddedDate,today).toTotalMonths();

            if(accumulatedMonthsFromLastInterestUpdate >= INTERESTFRECUENCYMONTH){
                /** new balance calculation **/
                interestRateAccrued = interestRateMonthly.multiply(new BigDecimal(accumulatedMonthsFromLastInterestUpdate));
                amountInterestRateAccrued = interestRateAccrued.multiply(creditCardAccount.getBalance().getAmount());
                BigDecimal newBalance = creditCardAccount.getBalance().getAmount().add(amountInterestRateAccrued);

                /** Setting and keeping new balance and date of last interest calculation**/
                creditCardAccount.setBalance(new Money(newBalance,creditCardAccount.getBalance().getCurrency()));

                LocalDate interestCalculationDate = lastInterestAddedDate.plusMonths(accumulatedMonthsFromLastInterestUpdate);

                creditCardAccount.setDateLastInterestAdded(interestCalculationDate);
                creditCardAccountRepository.save(creditCardAccount);
            }

        }

    }
}

