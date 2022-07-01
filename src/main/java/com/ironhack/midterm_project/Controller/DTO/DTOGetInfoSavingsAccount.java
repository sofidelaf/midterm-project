package com.ironhack.midterm_project.Controller.DTO;

import com.ironhack.midterm_project.Model.Embedded.Money;

import java.math.BigDecimal;

public class DTOGetInfoSavingsAccount {

    private Long id;
    private Money balance;
    private String primaryOwnerName;
    private String secondaryOwnerName;
    private String secretKey;
    private Money minimumBalance;
    private BigDecimal interestRate;

    // Constructor method

    public DTOGetInfoSavingsAccount(Long id, Money balance, String primaryOwnerName, String secondaryOwnerName, String secretKey, Money minimumBalance, BigDecimal interestRate) {
        this.id = id;
        this.balance = balance;
        this.primaryOwnerName = primaryOwnerName;
        this.secondaryOwnerName = secondaryOwnerName;
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public String getPrimaryOwnerName() {
        return primaryOwnerName;
    }

    public void setPrimaryOwnerName(String primaryOwnerName) {
        this.primaryOwnerName = primaryOwnerName;
    }

    public String getSecondaryOwnerName() {
        return secondaryOwnerName;
    }

    public void setSecondaryOwnerName(String secondaryOwnerName) {
        this.secondaryOwnerName = secondaryOwnerName;
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
        this.interestRate = interestRate;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
