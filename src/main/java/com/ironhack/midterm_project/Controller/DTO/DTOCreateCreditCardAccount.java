package com.ironhack.midterm_project.Controller.DTO;

import com.ironhack.midterm_project.Model.Embedded.Money;

import java.math.BigDecimal;

public class DTOCreateCreditCardAccount {

    private Long primaryOwnerUserId;

    private Long secondaryOwnerUserId;

    private String secretKey;

    private BigDecimal interestRate;

    private Money creditLimit;

    // Constructor method

    public DTOCreateCreditCardAccount(Long primaryOwnerUserId, Long secondaryOwnerUserId, String secretKey, BigDecimal interestRate, Money creditLimit) {
        this.primaryOwnerUserId = primaryOwnerUserId;
        this.secondaryOwnerUserId = secondaryOwnerUserId;
        this.secretKey = secretKey;
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }
}