package com.ironhack.midterm_project.Service.Impl;

import com.ironhack.midterm_project.Model.Accounts.Account;
import com.ironhack.midterm_project.Model.Accounts.Transaction;
import com.ironhack.midterm_project.Repository.TransactionRepository;
import com.ironhack.midterm_project.Service.Interface.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private final int PERIOD_FRAUD_SECONDS = 1;
    private int MAX_NUMBER_TRANSACTION_PER_PERIOD = 2;

    public boolean multipleTransactionFraud(Transaction transaction, Account account) {

        LocalDateTime initialCheckPeriod = transaction.getTimestamp().minusSeconds(PERIOD_FRAUD_SECONDS);

        int numberOfTransactionsInFraudPeriod = transactionRepository.getNumberOFTransactionsInAOneSecondPeriod(
                account,
                initialCheckPeriod,
                transaction.getTimestamp()
        );

        if(numberOfTransactionsInFraudPeriod> MAX_NUMBER_TRANSACTION_PER_PERIOD) {
            return true;
        } else {
            return false;
        }
    }

    private final BigDecimal LIMIT_DAILY_FACTOR = new BigDecimal(1.5);

    public boolean dailyQuantityOfTransactionsFraud(Transaction transaction, Account account){

        LocalDate DayOfTransaction = transaction.getTimestamp().toLocalDate();
        LocalDate creationDate = account.getCreationDate().toLocalDate();

        int historicNumberOfDaysFromCreation = Period.between(creationDate, DayOfTransaction).getDays();

        /** Calculation of Historical Number Of Transactions**/
        int historicMaximumNumberOfTransactionPerDay = 3;/** Default number **/

        LocalDateTime initialPeriod = LocalDateTime.of(creationDate, LocalTime.of(0,0));
        LocalDateTime finalPeriod = initialPeriod.plusDays(1);

        for(int i = 0;i<historicNumberOfDaysFromCreation;i++){ //Do not want to consider the transaction period
            finalPeriod = initialPeriod.plusDays(1);
            int dailyNumberOfTransactions = transactionRepository.getNumberOFTransactionsInAOneSecondPeriod(
                    account,
                    initialPeriod,
                    finalPeriod
            );

            if(dailyNumberOfTransactions > historicMaximumNumberOfTransactionPerDay){ historicMaximumNumberOfTransactionPerDay = dailyNumberOfTransactions;}

            initialPeriod = finalPeriod;
        }

        /** Calculation of Actual Number Of Transactions**/
        LocalDateTime actualInitialPeriod = transaction.getTimestamp().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime actualFinalPeriod = actualInitialPeriod.plusDays(1);
        BigDecimal actualDailyNumberOfTransactions = new BigDecimal(transactionRepository.getNumberOFTransactionsInAOneSecondPeriod(
                account,
                actualInitialPeriod,
                actualFinalPeriod
        ));

        /**Fraud condition**/
        BigDecimal limitDailyNumberOfTransactions = new BigDecimal(historicMaximumNumberOfTransactionPerDay).multiply(LIMIT_DAILY_FACTOR).setScale(2, RoundingMode.HALF_EVEN);

        if(actualDailyNumberOfTransactions.compareTo(limitDailyNumberOfTransactions) == 1 ){
            return true;
        } else {
            return false;
        }

    }

    public void setMAX_NUMBER_TRANSACTION_PER_PERIOD(int MAX_NUMBER_TRANSACTION_PER_PERIOD) {
        this.MAX_NUMBER_TRANSACTION_PER_PERIOD = MAX_NUMBER_TRANSACTION_PER_PERIOD;
    }
}

