package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Controller.DTO.DTOGetInfoCheckingAccount;
import com.ironhack.midterm_project.Controller.Interface.Enum.Status;
import com.ironhack.midterm_project.Model.Accounts.CheckingAccount;
import com.ironhack.midterm_project.Model.Accounts.StudentCheckingAccount;
import com.ironhack.midterm_project.Model.Embedded.Money;
import com.ironhack.midterm_project.Model.Users.AccountHolder;
import com.ironhack.midterm_project.Repository.AccountHolderRepository;
import com.ironhack.midterm_project.Repository.CheckingAccountRepository;
import com.ironhack.midterm_project.Repository.StudentCheckingAccountRepository;
import com.ironhack.midterm_project.Service.Interface.CheckingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class CheckingAccountServiceImpl implements CheckingAccountService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private StudentCheckingAccountRepository studentCheckingAccountRepository;

    @Autowired
    private AccountServiceImpl accountService;

    public Object createCheckingOrStudentAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey) {

        final int LIMITSTUDENTACCOUNT = 24;

        // User validation
        AccountHolder primaryOwner = accountHolderRepository.findById(primaryOwnerUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user id: " + primaryOwnerUserId + " does not exist in the database."));

        LocalDate today = LocalDate.now();
        LocalDate dayOfBirth = primaryOwner.getDateOfBirth();

        if (Period.between(dayOfBirth, today).getYears() >= LIMITSTUDENTACCOUNT) {
            return createCheckingAccount(primaryOwnerUserId, secondaryOwnerUserId, initialBalance, secretKey);
        } else {
            return createStudentCheckingAccount(primaryOwnerUserId, secondaryOwnerUserId, initialBalance, secretKey);
        }
    }


    public CheckingAccount createCheckingAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey){
        final BigDecimal defaultAmountMinimumBalance = new BigDecimal(250);
        final Currency defaultCurrencyMinimumBalance = Currency.getInstance("EUR");

        final BigDecimal defaultMaintenanceFeeAmount = new BigDecimal(12);
        final Currency eur = Currency.getInstance("EUR");

        CheckingAccount newAccount = new CheckingAccount();

        /**User validation and instantiation**/

        AccountHolder primaryOwner = accountHolderRepository.findById(primaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+primaryOwnerUserId + " does not exist in the Database"));
        newAccount.setPrimaryOwner(primaryOwner);

        AccountHolder secondaryOwner = null;
        if(secondaryOwnerUserId != null){
            secondaryOwner = accountHolderRepository.findById(secondaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+secondaryOwnerUserId + " does not exist in the Database"));
        }
        newAccount.setSecondaryOwner(secondaryOwner);

        /**Default minimumBalance **/


        Money createMinimumBalance = new Money(defaultAmountMinimumBalance,defaultCurrencyMinimumBalance);
        newAccount.setMinimumBalance(createMinimumBalance);

        /**Initial Balance**/
        Money createInitialBalance;
        //lower initial balance than the minimum; null is already covered with Model Validation Not Null
        if(initialBalance != null && initialBalance.getAmount().compareTo(createMinimumBalance.getAmount()) == -1){
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

        /**Monthly Maintenance Fee**/


        Money monthlyMaintenanceFee = new Money(defaultMaintenanceFeeAmount,eur);
        newAccount.setMonthlyMaintenanceFee(monthlyMaintenanceFee);

        checkingAccountRepository.save(newAccount);

        return checkingAccountRepository.findById(newAccount.getId()).get() ;
    }

    public StudentCheckingAccount createStudentCheckingAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey){
        StudentCheckingAccount newAccount = new StudentCheckingAccount();

        /**User validation and instantiation**/

        AccountHolder primaryOwner = accountHolderRepository.findById(primaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+primaryOwnerUserId + " does not exist in the Database"));
        newAccount.setPrimaryOwner(primaryOwner);

        AccountHolder secondaryOwner = null;
        if(secondaryOwnerUserId != null){
            secondaryOwner = accountHolderRepository.findById(secondaryOwnerUserId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user id: "+secondaryOwnerUserId + " does not exist in the Database"));
        }
        newAccount.setSecondaryOwner(secondaryOwner);


        /**Initial Balance**/

        Money createInitialBalance;
        if (initialBalance == null){
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



        return studentCheckingAccountRepository.save(newAccount);
    }


    public List<DTOGetInfoCheckingAccount> getPersonalAccountInformation(String username) {

        AccountHolder accountHolder = accountHolderRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Account Holder with username: " + username));

        List<DTOGetInfoCheckingAccount> infoCheckingAccountList = new ArrayList<>();

        for(CheckingAccount checkingAccount : checkingAccountRepository.findByPrimaryOwner(accountHolder)){
            /** interest check and update **/
            monthlyFeeUpdate(checkingAccount);
            /** minimum balance check and update **/
            accountService.applicablePenaltyFee(checkingAccount);


            String secondaryOwnerName = checkingAccount.getSecondaryOwner() != null ? checkingAccount.getSecondaryOwner().getName() : null;

            DTOGetInfoCheckingAccount newDTO = new DTOGetInfoCheckingAccount(
                    checkingAccount.getId(),
                    checkingAccount.getBalance(),
                    checkingAccount.getPrimaryOwner().getName(),
                    secondaryOwnerName,
                    checkingAccount.getSecretKey(),
                    checkingAccount.getMinimumBalance(),
                    checkingAccount.getMonthlyMaintenanceFee()
            );
            infoCheckingAccountList.add(newDTO);
        }

        for(CheckingAccount checkingAccount: checkingAccountRepository.findBySecondaryOwner(accountHolder)){
            /** interest check and update **/
            monthlyFeeUpdate(checkingAccount);
            /** minimum balance check and update **/
            accountService.applicablePenaltyFee(checkingAccount);


            DTOGetInfoCheckingAccount newDTO = new DTOGetInfoCheckingAccount(
                    checkingAccount.getId(),
                    checkingAccount.getBalance(),
                    checkingAccount.getPrimaryOwner().getName(),
                    checkingAccount.getSecondaryOwner().getName(),
                    checkingAccount.getSecretKey(),
                    checkingAccount.getMinimumBalance(),
                    checkingAccount.getMonthlyMaintenanceFee()
            );
            infoCheckingAccountList.add(newDTO);
        }

        return infoCheckingAccountList;
    }

    private final int MAINTENANCE_FREQUENCY_FEE_MONTHS = 1;

    /**Variables for interest Rate **/
    private BigDecimal monthlyMaintenanceFee;
    private LocalDate today = LocalDate.now();
    private LocalDate accountCreationDate;
    private LocalDate lastMonthlyMaintenanceAddedDate;
    private long accumulatedMonthsFromCreation;
    private long accumulatedMonthsFromLastMaintenanceFeeUpdate;

    private BigDecimal maintenanceFeeAccrued;


    public void monthlyFeeUpdate(CheckingAccount checkingAccount) {

        /** Variables **/
        monthlyMaintenanceFee = checkingAccount.getMonthlyMaintenanceFee().getAmount();
        accountCreationDate = checkingAccount.getCreationDate().toLocalDate();
        lastMonthlyMaintenanceAddedDate = checkingAccount.getDateLastMonthlyMaintenanceFeeApplied();

        /** Period in years calculation **/
        accumulatedMonthsFromCreation = Period.between(accountCreationDate,today).toTotalMonths();

        /** Update from creation **/
        if(lastMonthlyMaintenanceAddedDate == null){
            if(accumulatedMonthsFromCreation >= MAINTENANCE_FREQUENCY_FEE_MONTHS){
                /** new balance calculation **/
                maintenanceFeeAccrued = monthlyMaintenanceFee.multiply(new BigDecimal(accumulatedMonthsFromCreation));
                BigDecimal newBalance = checkingAccount.getBalance().getAmount().subtract(maintenanceFeeAccrued);

                /** Setting and keeping new balance and date of last interest calculation**/
                checkingAccount.setBalance(new Money(newBalance,checkingAccount.getBalance().getCurrency()));

                LocalDate maintenanceFeeApplicableDate = accountCreationDate.plusMonths(accumulatedMonthsFromCreation);

                checkingAccount.setDateLastMonthlyMaintenanceFeeApplied(maintenanceFeeApplicableDate);
                checkingAccountRepository.save(checkingAccount);
            }

        } else {/** Update from last time**/
            accumulatedMonthsFromLastMaintenanceFeeUpdate = Period.between(lastMonthlyMaintenanceAddedDate,today).toTotalMonths();

            if(accumulatedMonthsFromLastMaintenanceFeeUpdate >= MAINTENANCE_FREQUENCY_FEE_MONTHS){
                /** new balance calculation **/
                maintenanceFeeAccrued = monthlyMaintenanceFee.multiply(new BigDecimal(accumulatedMonthsFromLastMaintenanceFeeUpdate));
                BigDecimal newBalance = checkingAccount.getBalance().getAmount().subtract(maintenanceFeeAccrued);

                /** Setting and keeping new balance and date of last interest calculation**/
                checkingAccount.setBalance(new Money(newBalance,checkingAccount.getBalance().getCurrency()));

                LocalDate maintenanceFeeApplicableDate = lastMonthlyMaintenanceAddedDate.plusMonths(accumulatedMonthsFromLastMaintenanceFeeUpdate);

                checkingAccount.setDateLastMonthlyMaintenanceFeeApplied(maintenanceFeeApplicableDate);
                checkingAccountRepository.save(checkingAccount);
            }
        }
    }
}

