package com.ironhack.midterm_project.Controller.DTO;

import com.ironhack.midterm_project.Model.Embedded.Money;

import java.math.BigDecimal;

public class DTOGetInfoCreditCardsAccount {

    private Long id;
    private Money balance;
    private String primaryOwnerName;
    private String secondaryOwnerName;
    private String secretKey;
    private Money creditLimit;
    private BigDecimal interestRate;

    // Constructor method


    public DTOGetInfoCreditCardsAccount(Long id, Money balance, String primaryOwnerName, String secondaryOwnerName, String secretKey, Money creditLimit, BigDecimal interestRate) {
        this.id = id;
        this.balance = balance;
        this.primaryOwnerName = primaryOwnerName;
        this.secondaryOwnerName = secondaryOwnerName;
        this.secretKey = secretKey;
        this.creditLimit = creditLimit;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
