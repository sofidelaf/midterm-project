package com.ironhack.midterm_project.Controller.DTO;

import com.ironhack.midterm_project.Model.Embedded.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DTOCreateSavingsAccount {

    private Long primaryOwnerUserId;

    private Long secondaryOwnerUserId;

    private Money initialBalance;

    private String secretKey;

    private Money minimumBalance;

    private BigDecimal interestRate;

    // Constructor method

    public DTOCreateSavingsAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, Money initialBalance, String secretKey, Money minimumBalance, BigDecimal interestRate) {
        this.primaryOwnerUserId = primaryOwnerUserId;
        this.secondaryOwnerUserId = secondaryOwnerUserId;
        this.initialBalance = initialBalance;
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
    }


    // Getters and setters


    public Long getPrimaryOwnerUserId() {
        return primaryOwnerUserId;
    }

    public void setPrimaryOwnerUserId(Long primaryOwnerUserId) {
        this.primaryOwnerUserId = primaryOwnerUserId;
    }

    public Long getSecondaryOwnerUserId() {
        return secondaryOwnerUserId;
    }

    public void setSecondaryOwnerUserId(Long secondaryOwnerUserId) {
        this.secondaryOwnerUserId = secondaryOwnerUserId;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Money initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate.setScale(4, RoundingMode.HALF_EVEN);
    }
}