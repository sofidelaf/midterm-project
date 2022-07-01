package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoSavingsAccount;
import com.ironhack.midterm_project.Controller.Interface.Enum.Status;
import com.ironhack.midterm_project.Model.Accounts.SavingsAccount;
import com.ironhack.midterm_project.Model.Embedded.Money;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.SavingsAccountRepository;
import com.ironhack.midterm_project.Service.Interface.SavingsAccountService;
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
public class SavingsAccountServiceImpl implements SavingsAccountService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    @Autowired
    private AccountServiceImpl accountService;

    public SavingsAccount createSavingsAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey, Money minimumBalance, BigDecimal interestRate) {

        final BigDecimal defaultAmountMinimumBalance = new BigDecimal(1000);
        final Currency defaultCurrencyMinimumBalance = Currency.getInstance("EUR");
        final BigDecimal limitMinimumBalanceValue = new BigDecimal(100);

        final BigDecimal defaultInterestRate = new BigDecimal(0.0025).setScale(4, RoundingMode.HALF_EVEN);
        final BigDecimal limitMaximumInterestRate = new BigDecimal(0.5);

        SavingsAccount newAccount = new SavingsAccount();

        /**User validation and instantiation**/

        AccountHolder primaryOwner = accountHolderRepository.findById(primaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+primaryOwnerUserId + " does not exist in the Database"));
        newAccount.setPrimaryOwner(primaryOwner);

        AccountHolder secondaryOwner = null;
        if(secondaryOwnerUserId != null){
            secondaryOwner = accountHolderRepository.findById(secondaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+secondaryOwnerUserId + " does not exist in the Database"));
        }
        newAccount.setSecondaryOwner(secondaryOwner);

        /**Default minimumBalance and definition**/
        Money createMinimumBalance;



        if(minimumBalance == null){
            createMinimumBalance = new Money(defaultAmountMinimumBalance,defaultCurrencyMinimumBalance);
        } else if(minimumBalance.getAmount().compareTo(limitMinimumBalanceValue) < 0){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"Minimum balance cannot be lower than "+limitMinimumBalanceValue.intValue());
        } else {
            createMinimumBalance = minimumBalance;
        }
        newAccount.setMinimumBalance(createMinimumBalance);

        /**Initial Balance**/
        Money createInitialBalance;
        //lower initial balance than the minimum; null is already covered with Model Validation Not Null
        if(initialBalance != null && initialBalance.getAmount().compareTo(createMinimumBalance.getAmount()) < 0){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Initial balance cannot be lower than minimum Balance: "+createMinimumBalance.getAmount().intValue());
        } else if (initialBalance == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Initial balance cannot be null");
        } else {
            createInitialBalance = initialBalance;
        }
        newAccount.setBalance(createInitialBalance);

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
        } else if(interestRate.compareTo(limitMaximumInterestRate) == 1){  //value provided greater than the limit returns 1
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"Interest Rate cannot be greater than maximum value of: "+limitMaximumInterestRate);
        } else {
            createInterestRate = interestRate.setScale(4,RoundingMode.HALF_EVEN);
        }
        newAccount.setInterestRate(createInterestRate);

        return savingsAccountRepository.save(newAccount);
    }


    public List<DTOGetInfoSavingsAccount> getPersonalAccountInformation(String username) {

        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Account Holder with username: " + username));

        List<DTOGetInfoSavingsAccount> infoSavingsAccountList = new ArrayList<>();

        for(SavingsAccount savingsAccount: savingsAccountRepository.findByPrimaryOwner(accountHolder)){
            /** interest check and update **/
            interestUpdate(savingsAccount);
            /** minimum balance check and update **/
            accountService.applicablePenaltyFee(savingsAccount);

            String secondaryOwnerName = savingsAccount.getSecondaryOwner() != null ? savingsAccount.getSecondaryOwner().getName() : null;

            DTOGetInfoSavingsAccount newDTO = new DTOGetInfoSavingsAccount(
                    savingsAccount.getId(),
                    savingsAccount.getBalance(),
                    savingsAccount.getPrimaryOwner().getName(),
                    secondaryOwnerName,
                    savingsAccount.getSecretKey(),
                    savingsAccount.getMinimumBalance(),
                    savingsAccount.getInterestRate()
            );
            infoSavingsAccountList.add(newDTO);
        }

        for(SavingsAccount savingsAccount: savingsAccountRepository.findBySecondaryOwner(accountHolder)){
            /** interest check and update **/
            interestUpdate(savingsAccount);
            /** minimum balance check and update **/
            accountService.applicablePenaltyFee(savingsAccount);


            DTOGetInfoSavingsAccount newDTO = new DTOGetInfoSavingsAccount(
                    savingsAccount.getId(),
                    savingsAccount.getBalance(),
                    savingsAccount.getPrimaryOwner().getName(),
                    savingsAccount.getSecondaryOwner().getName(),
                    savingsAccount.getSecretKey(),
                    savingsAccount.getMinimumBalance(),
                    savingsAccount.getInterestRate()
            );
            infoSavingsAccountList.add(newDTO);
        }

        return infoSavingsAccountList;
    }

    final int INTEREST_FREQUENCY_YEAR = 1;

    /**Variables for interest Rate **/
    private BigDecimal interestRate;
    private LocalDate today = LocalDate.now();
    private LocalDate accountCreationDate;
    private LocalDate lastInterestAddedDate;
    private int accumulatedYearsFromCreation;
    private int accumulatedYearsFromLastInterestUpdate;

    private BigDecimal interestRateAccrued;
    private BigDecimal amountInterestRateAccrued;

    public void interestUpdate(SavingsAccount savingsAccount) {
        /** Variables **/
        interestRate = savingsAccount.getInterestRate();
        accountCreationDate = savingsAccount.getCreationDate().toLocalDate();
        lastInterestAddedDate = savingsAccount.getDateLastInterestAdded();

        /** Period in years calculation **/
        accumulatedYearsFromCreation = Period.between(accountCreationDate,today).getYears();


        /** Update from creation **/
        if(lastInterestAddedDate == null){
            if(accumulatedYearsFromCreation >= INTEREST_FREQUENCY_YEAR){
                /** new balance calculation **/
                interestRateAccrued = interestRate.multiply(new BigDecimal(accumulatedYearsFromCreation));
                amountInterestRateAccrued = interestRateAccrued.multiply(savingsAccount.getBalance().getAmount());
                BigDecimal newBalance = savingsAccount.getBalance().getAmount().add(amountInterestRateAccrued);

                /** Setting and keeping new balance and date of last interest calculation**/
                savingsAccount.setBalance(new Money(newBalance,savingsAccount.getBalance().getCurrency()));

                LocalDate interestCalculationDate = accountCreationDate.plusYears(accumulatedYearsFromCreation);
                savingsAccount.setDateLastInterestAdded(interestCalculationDate);
                savingsAccountRepository.save(savingsAccount);
            }

        } else{ /** Update from last time**/
            accumulatedYearsFromLastInterestUpdate = Period.between(lastInterestAddedDate,today).getYears();

            if(accumulatedYearsFromLastInterestUpdate >= INTEREST_FREQUENCY_YEAR){
                /** new balance calculation **/
                interestRateAccrued = interestRate.multiply(new BigDecimal(accumulatedYearsFromLastInterestUpdate));
                amountInterestRateAccrued = interestRateAccrued.multiply(savingsAccount.getBalance().getAmount());
                BigDecimal newBalance = savingsAccount.getBalance().getAmount().add(amountInterestRateAccrued);

                /** Setting and keeping new balance and date of last interest calculation**/
                savingsAccount.setBalance(new Money(newBalance,savingsAccount.getBalance().getCurrency()));

                LocalDate interestCalculationDate = lastInterestAddedDate.plusYears(accumulatedYearsFromLastInterestUpdate);
                savingsAccount.setDateLastInterestAdded(interestCalculationDate);
                savingsAccountRepository.save(savingsAccount);
            }

        }

    }
}
