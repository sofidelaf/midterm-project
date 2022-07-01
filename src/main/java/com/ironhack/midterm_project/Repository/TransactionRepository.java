package com.ironhack.midterm_project.Repository;

import com.ironhack.midterm_project.Model.Accounts.Account;
import com.ironhack.midterm_project.Model.Accounts.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COUNT(*) FROM Transaction t " +
            "WHERE (t.senderAccount = :account AND t.timestamp BETWEEN :initialPeriod AND :timestamp)" +
            "OR (t.recipientAccount = :account AND t.timestamp BETWEEN :initialPeriod AND :timestamp)")
    int getNumberOFTransactionsInAOneSecondPeriod(
            @Param("account") Account account,
            @Param("initialPeriod") LocalDateTime initialPeriod,
            @Param("timestamp") LocalDateTime timestamp
    );


}